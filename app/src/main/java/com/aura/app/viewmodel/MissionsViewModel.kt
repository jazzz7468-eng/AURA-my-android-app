package com.aura.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.app.data.missions.MissionData
import com.aura.app.data.missions.QuizData
import com.aura.app.data.missions.QuizQuestion
import com.aura.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class MissionsUiState(
    val completedMissionIds: Set<String> = emptySet(),
    val currentQuiz: QuizQuestion? = null,
    val isSpinning: Boolean = false,
    val quizResult: Boolean? = null,     // null = not answered, true/false
    val selectedAnswer: Int? = null,
    val reflectionText: String = "",
    val showReflectionDialog: Boolean = false,
    val reflectionType: String = "",       // "reflect" or "kindness"
)

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()

    private val _stageUpEvent = MutableSharedFlow<Int>()
    val stageUpEvent: SharedFlow<Int> = _stageUpEvent.asSharedFlow()

    val userProfile = repository.userProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    init {
        loadCompletedMissions()
    }

    private fun loadCompletedMissions() {
        viewModelScope.launch {
            val todayStart = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val completedIds = repository.getCompletedMissionIdsSince(todayStart)
            _uiState.update { it.copy(completedMissionIds = completedIds.toSet()) }
        }
    }

    fun completeMission(missionId: String, xpReward: Int) {
        if (_uiState.value.completedMissionIds.contains(missionId)) return

        viewModelScope.launch {
            repository.completeMission(missionId, xpReward)
            val newStage = repository.awardXP(xpReward)

            _uiState.update {
                it.copy(completedMissionIds = it.completedMissionIds + missionId)
            }

            if (newStage != null) {
                _stageUpEvent.emit(newStage)
            }

            // Check if all 3 daily missions are now complete (for "Triple Crown")
            val dailyIds = MissionData.dailyMissions.map { it.id }.toSet()
            val completedDaily = _uiState.value.completedMissionIds.intersect(dailyIds)
            if (completedDaily.size == 2 && missionId in dailyIds) {
                // Auto-complete the "Triple Crown" daily_3 mission
                if (!_uiState.value.completedMissionIds.contains("daily_3")) {
                    completeMission("daily_3", 100)
                }
            }
        }
    }

    fun spinWheel() {
        _uiState.update {
            it.copy(
                isSpinning = true,
                currentQuiz = null,
                quizResult = null,
                selectedAnswer = null,
            )
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Spin animation duration
            val question = QuizData.getRandomQuestion()
            _uiState.update {
                it.copy(isSpinning = false, currentQuiz = question)
            }
        }
    }

    fun answerQuiz(selectedIndex: Int) {
        val quiz = _uiState.value.currentQuiz ?: return
        val isCorrect = selectedIndex == quiz.correctIndex

        _uiState.update {
            it.copy(quizResult = isCorrect, selectedAnswer = selectedIndex)
        }

        if (isCorrect) {
            completeMission("quick_spin", 25)
        }
    }

    fun dismissQuiz() {
        _uiState.update {
            it.copy(currentQuiz = null, quizResult = null, selectedAnswer = null)
        }
    }

    fun showReflection(type: String) {
        _uiState.update {
            it.copy(showReflectionDialog = true, reflectionType = type, reflectionText = "")
        }
    }

    fun updateReflectionText(text: String) {
        _uiState.update { it.copy(reflectionText = text) }
    }

    fun submitReflection() {
        val type = _uiState.value.reflectionType
        val missionId = if (type == "reflect") "quick_reflect" else "quick_kindness"
        val xp = if (type == "reflect") 30 else 40

        completeMission(missionId, xp)
        _uiState.update { it.copy(showReflectionDialog = false, reflectionText = "") }
    }

    fun dismissReflection() {
        _uiState.update { it.copy(showReflectionDialog = false) }
    }
}
