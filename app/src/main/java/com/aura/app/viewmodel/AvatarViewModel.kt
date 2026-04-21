package com.aura.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aura.app.data.local.entities.UserProfile
import com.aura.app.data.repository.UserRepository
import com.aura.app.util.XPManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class AvatarUiState(
    val avatarStage: Int = 1,
    val xp: Int = 0,
    val xpProgress: Float = 0f,
    val xpToNext: Int = 200,
    val skills: Map<String, Int> = emptyMap(),
    val stageName: String = "Seed",
    val stageEmoji: String = "🌱",
    val stageDescription: String = "",
    val totalSessions: Int = 0,
    val totalMissions: Int = 0,
    val streakDays: Int = 0,
)

@HiltViewModel
class AvatarViewModel @Inject constructor(
    repository: UserRepository,
) : ViewModel() {

    val uiState: Flow<AvatarUiState> = repository.userProfile.map { profile ->
        profile?.toAvatarUiState() ?: AvatarUiState()
    }

    private fun UserProfile.toAvatarUiState(): AvatarUiState {
        return AvatarUiState(
            avatarStage = avatarStage,
            xp = xp,
            xpProgress = XPManager.getXPProgressToNextStage(xp),
            xpToNext = XPManager.getXPForNextStage(xp) - xp,
            skills = mapOf(
                "Empathy" to empathy,
                "Confidence" to confidence,
                "Communication" to communication,
                "Leadership" to leadership,
                "Resilience" to resilience,
            ),
            stageName = XPManager.getStageName(avatarStage),
            stageEmoji = XPManager.getStageEmoji(avatarStage),
            stageDescription = XPManager.getStageDescription(avatarStage, ageGroup),
            totalSessions = totalSessions,
            totalMissions = totalMissionsCompleted,
            streakDays = streakDays,
        )
    }
}
