package com.aura.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.app.BuildConfig
import com.aura.app.data.local.entities.SocialLabSession
import com.aura.app.data.remote.*
import com.aura.app.data.repository.UserRepository
import com.aura.app.data.scenarios.ScenarioData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val role: String, // "user" or "ai"
    val content: String,
    val scores: Map<String, Int>? = null,
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSessionComplete: Boolean = false,
    val turnCount: Int = 0,
    val scenarioTitle: String = "",
    val maxTurns: Int = 8,

    // Aggregate scores
    val overallScore: Int = 0,
    val skillScores: Map<String, Int> = emptyMap(),
    val xpEarned: Int = 0,
    val strengths: List<String> = emptyList(),
    val improvements: List<String> = emptyList(),
)

@HiltViewModel
class SocialLabViewModel @Inject constructor(
    private val apiService: GeminiApiService,
    private val repository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _stageUpEvent = MutableSharedFlow<Int>()
    val stageUpEvent: SharedFlow<Int> = _stageUpEvent.asSharedFlow()

    private var systemPrompt = ""
    private var conversationHistory = mutableListOf<GeminiContent>()
    private var currentScenarioId = ""
    private val allScores = mutableListOf<Map<String, Int>>()
    private val gson = Gson()

    fun startSession(scenarioId: String) {
        val scenario = ScenarioData.getScenarioById(scenarioId) ?: return
        currentScenarioId = scenarioId
        systemPrompt = scenario.systemPrompt

        conversationHistory.clear()
        allScores.clear()

        // Add system context as the first "user" message (Gemini uses user/model roles)
        conversationHistory.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart("System context: $systemPrompt\n\nStart the roleplay with your opening message."))
            )
        )
        conversationHistory.add(
            GeminiContent(
                role = "model",
                parts = listOf(GeminiPart(scenario.openingMessage))
            )
        )

        _uiState.value = ChatUiState(
            messages = listOf(
                ChatMessage(role = "ai", content = scenario.openingMessage)
            ),
            scenarioTitle = scenario.title,
            maxTurns = scenario.maxTurns,
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(role = "user", content = text)
        val currentMessages = _uiState.value.messages + userMessage

        _uiState.update {
            it.copy(
                messages = currentMessages,
                isLoading = true,
                turnCount = it.turnCount + 1,
            )
        }

        conversationHistory.add(
            GeminiContent(role = "user", parts = listOf(GeminiPart(text)))
        )

        viewModelScope.launch {
            try {
                val request = GeminiRequest(contents = conversationHistory)
                val response = apiService.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request,
                )

                val aiText = response.getText()
                val (cleanText, scores) = parseResponseAndScores(aiText)

                conversationHistory.add(
                    GeminiContent(role = "model", parts = listOf(GeminiPart(aiText)))
                )

                if (scores != null) {
                    allScores.add(scores)
                }

                val aiMessage = ChatMessage(role = "ai", content = cleanText, scores = scores)

                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                // Fallback mock response
                val mockResponse = getMockResponse(_uiState.value.turnCount)
                val aiMessage = ChatMessage(role = "ai", content = mockResponse)

                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun endSession() {
        val scenario = ScenarioData.getScenarioById(currentScenarioId) ?: return

        // Calculate aggregate scores
        val avgScores = mutableMapOf<String, Int>()
        if (allScores.isNotEmpty()) {
            val allKeys = allScores.flatMap { it.keys }.distinct()
            allKeys.forEach { key ->
                val values = allScores.mapNotNull { it[key] }
                avgScores[key] = if (values.isNotEmpty()) values.average().toInt() * 10 else 70
            }
        } else {
            // Mock scores if API didn't return scores
            scenario.skills.forEach { skill ->
                avgScores[skill] = (60..85).random()
            }
        }

        val overallScore = if (avgScores.isNotEmpty()) avgScores.values.average().toInt() else 72
        val xpEarned = (scenario.xpReward * overallScore / 100f).toInt().coerceAtLeast(10)

        _uiState.update {
            it.copy(
                isSessionComplete = true,
                overallScore = overallScore,
                skillScores = avgScores,
                xpEarned = xpEarned,
                strengths = generateStrengths(avgScores),
                improvements = generateImprovements(avgScores),
            )
        }

        // Save session and award XP
        viewModelScope.launch {
            repository.saveSession(
                SocialLabSession(
                    scenarioId = currentScenarioId,
                    scenarioTitle = scenario.title,
                    overallScore = overallScore,
                    empathyScore = avgScores["empathy"] ?: 0,
                    confidenceScore = avgScores["confidence"] ?: 0,
                    communicationScore = avgScores["communication"] ?: 0,
                    leadershipScore = avgScores["leadership"] ?: 0,
                    resilienceScore = avgScores["resilience"] ?: 0,
                    xpEarned = xpEarned,
                )
            )

            // Award XP and check for avatar evolution
            val newStage = repository.awardXP(xpEarned)
            if (newStage != null) {
                _stageUpEvent.emit(newStage)
            }

            // Update skill scores (scaled down for gradual progress)
            repository.updateSkills(
                empathy = (avgScores["empathy"] ?: 0) / 10,
                confidence = (avgScores["confidence"] ?: 0) / 10,
                communication = (avgScores["communication"] ?: 0) / 10,
                leadership = (avgScores["leadership"] ?: 0) / 10,
                resilience = (avgScores["resilience"] ?: 0) / 10,
            )
        }
    }

    private fun parseResponseAndScores(text: String): Pair<String, Map<String, Int>?> {
        // Try to find JSON score at the end of the response
        val jsonRegex = Regex("""\{[^}]*"[a-z]+":\s*\d+[^}]*\}""")
        val match = jsonRegex.findAll(text).lastOrNull()

        if (match != null) {
            val cleanText = text.substring(0, match.range.first).trim()
            try {
                @Suppress("UNCHECKED_CAST")
                val scoresMap = gson.fromJson(match.value, Map::class.java) as? Map<String, Double>
                val intScores = scoresMap?.mapValues { it.value.toInt() }
                return Pair(cleanText, intScores)
            } catch (_: Exception) { }
        }
        return Pair(text.trim(), null)
    }

    private fun generateStrengths(scores: Map<String, Int>): List<String> {
        return scores.filter { it.value >= 70 }.map { (skill, score) ->
            when (skill) {
                "empathy" -> "Great empathy — you showed genuine understanding"
                "confidence" -> "Strong confidence — you spoke with conviction"
                "communication" -> "Clear communication — your points were well-articulated"
                "leadership" -> "Good leadership — you took charge effectively"
                "resilience" -> "Solid resilience — you stayed composed under pressure"
                else -> "Strong $skill skills"
            }
        }.take(3).ifEmpty { listOf("Good effort! Keep practicing to improve.") }
    }

    private fun generateImprovements(scores: Map<String, Int>): List<String> {
        return scores.filter { it.value < 70 }.map { (skill, _) ->
            when (skill) {
                "empathy" -> "Try to acknowledge others' feelings more directly"
                "confidence" -> "Practice speaking more assertively"
                "communication" -> "Be more specific and concise in your responses"
                "leadership" -> "Take more initiative in guiding the conversation"
                "resilience" -> "Work on staying calm when challenged"
                else -> "Keep working on $skill"
            }
        }.take(3).ifEmpty { listOf("Great job! Try a harder scenario next time.") }
    }

    private fun getMockResponse(turn: Int): String {
        val mockResponses = listOf(
            "I appreciate you saying that. Can you tell me more about what you mean?",
            "That's an interesting perspective. How would you handle it if things got more difficult?",
            "I see where you're coming from. What would you do differently next time?",
            "That's a thoughtful response. Let me challenge you a bit — what if the situation escalated?",
            "Good point. I think you're showing real growth here. What did you learn from this?",
        )
        return mockResponses[turn % mockResponses.size]
    }
}
