package com.aura.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.app.data.local.MirrorDao
import com.aura.app.data.local.entities.MirrorSession
import com.aura.app.data.mirror.PoseAnalyzer
import com.aura.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MirrorUiState(
    // Session state
    val isSessionActive: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val sessionDurationTarget: Int = 60,  // Default 60 seconds

    // Real-time feedback
    val currentPosture: Int = 0,
    val currentEyeContact: Boolean = false,
    val currentGestures: Boolean = false,
    val liveConfidence: Int = 0,
    val feedbackMessage: String = "Position yourself in the frame",

    // Session results
    val showResults: Boolean = false,
    val sessionSummary: PoseAnalyzer.SessionSummary? = null,
    val xpEarned: Int = 0,

    // History
    val pastSessions: List<MirrorSession> = emptyList(),
    val totalSessions: Int = 0,
    val averageConfidence: Float? = null,
)

@HiltViewModel
class MirrorViewModel @Inject constructor(
    private val mirrorDao: MirrorDao,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MirrorUiState())
    val uiState: StateFlow<MirrorUiState> = _uiState.asStateFlow()

    // Collected frames for session analysis
    private val frameAnalyses = mutableListOf<PoseAnalyzer.FrameAnalysis>()
    private var timerJob: Job? = null

    private val _stageUpEvent = MutableSharedFlow<Int>()
    val stageUpEvent: SharedFlow<Int> = _stageUpEvent.asSharedFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            mirrorDao.getRecentSessions(20).collect { sessions ->
                val total = mirrorDao.getSessionCount()
                val avgConf = mirrorDao.getAverageConfidence()
                _uiState.update {
                    it.copy(
                        pastSessions = sessions,
                        totalSessions = total,
                        averageConfidence = avgConf,
                    )
                }
            }
        }
    }

    fun startSession(durationSeconds: Int = 60) {
        frameAnalyses.clear()
        _uiState.update {
            it.copy(
                isSessionActive = true,
                isPaused = false,
                elapsedSeconds = 0,
                sessionDurationTarget = durationSeconds,
                showResults = false,
                sessionSummary = null,
                feedbackMessage = "Session started! Stand tall and look at the camera.",
            )
        }

        // Start timer
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.elapsedSeconds < durationSeconds) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
            // Session complete
            endSession()
        }
    }

    fun pauseSession() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeSession() {
        _uiState.update { it.copy(isPaused = false) }
    }

    /**
     * Called every ~500ms from the camera analyzer with fresh frame data.
     */
    fun onFrameAnalyzed(analysis: PoseAnalyzer.FrameAnalysis) {
        if (!_uiState.value.isSessionActive || _uiState.value.isPaused) return

        frameAnalyses.add(analysis)

        // Update live feedback
        val recentFrames = frameAnalyses.takeLast(10)
        val avgPosture = recentFrames.map { it.postureScore }.average().toInt()
        val lookingPercent = recentFrames.count { it.isLookingStraight } * 10

        val feedbackMsg = when {
            avgPosture < 40 -> "⚠️ Straighten up! Pull your shoulders back."
            !analysis.isLookingStraight -> "👀 Look at the camera — maintain eye contact."
            !analysis.handsVisible -> "🤲 Use your hands! Gesture while you speak."
            avgPosture >= 80 && analysis.isLookingStraight -> "🌟 Perfect form! Keep it up!"
            else -> "👍 Looking good. Keep your posture steady."
        }

        _uiState.update {
            it.copy(
                currentPosture = analysis.postureScore,
                currentEyeContact = analysis.isLookingStraight,
                currentGestures = analysis.handsVisible,
                liveConfidence = (avgPosture * 0.5 + lookingPercent * 0.3 + (if (analysis.handsVisible) 20 else 0)).toInt().coerceIn(0, 100),
                feedbackMessage = feedbackMsg,
            )
        }
    }

    fun endSession() {
        timerJob?.cancel()
        _uiState.update { it.copy(isSessionActive = false) }

        val summary = PoseAnalyzer.summarizeSession(frameAnalyses)
        val xp = calculateXP(summary)

        viewModelScope.launch {
            // Save session to DB
            mirrorDao.insertSession(
                MirrorSession(
                    durationSeconds = _uiState.value.elapsedSeconds,
                    postureScore = summary.postureScore,
                    eyeContactScore = summary.eyeContactScore,
                    gestureScore = summary.gestureScore,
                    overallConfidence = summary.overallConfidence,
                    postureFeedback = summary.postureFeedback,
                    eyeContactFeedback = summary.eyeContactFeedback,
                    gestureFeedback = summary.gestureFeedback,
                    xpEarned = xp,
                )
            )

            // Award XP and check for stage-up
            val newStage = userRepository.awardXP(xp)
            if (newStage != null) {
                _stageUpEvent.emit(newStage)
            }

            // Update skills
            userRepository.updateSkills(
                confidence = (summary.overallConfidence / 20).coerceIn(1, 5),
                communication = (summary.eyeContactScore / 25).coerceIn(1, 4),
            )

            _uiState.update {
                it.copy(
                    showResults = true,
                    sessionSummary = summary,
                    xpEarned = xp,
                )
            }

            loadHistory()
        }
    }

    fun dismissResults() {
        _uiState.update { it.copy(showResults = false, sessionSummary = null) }
    }

    private fun calculateXP(summary: PoseAnalyzer.SessionSummary): Int {
        val base = 30
        val bonus = when {
            summary.overallConfidence >= 80 -> 20
            summary.overallConfidence >= 60 -> 10
            else -> 0
        }
        return base + bonus
    }
}
