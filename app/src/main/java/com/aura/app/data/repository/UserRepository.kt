package com.aura.app.data.repository

import com.aura.app.data.local.UserDao
import com.aura.app.data.local.entities.CompletedMission
import com.aura.app.data.local.entities.SocialLabSession
import com.aura.app.data.local.entities.UserProfile
import com.aura.app.util.XPManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
) {
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val completedMissions: Flow<List<CompletedMission>> = userDao.getCompletedMissions()
    val sessions: Flow<List<SocialLabSession>> = userDao.getSessions()

    suspend fun createProfile(name: String, ageGroup: String) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        userDao.upsertProfile(
            UserProfile(
                name = name,
                ageGroup = ageGroup,
                hasOnboarded = true,
                lastActiveDate = today,
                streakDays = 1,
            )
        )
    }

    suspend fun getProfile(): UserProfile? = userDao.getUserProfileSync()

    /**
     * Awards XP, updates level and avatar stage. Returns the new avatar stage if it changed (for celebration), null otherwise.
     */
    suspend fun awardXP(amount: Int): Int? {
        userDao.addXP(amount)
        val profile = userDao.getUserProfileSync() ?: return null

        val newLevel = XPManager.getLevelForXP(profile.xp)
        val newStage = XPManager.getStageForXP(profile.xp)

        if (newLevel != profile.level) {
            userDao.updateLevel(newLevel)
        }

        return if (newStage != profile.avatarStage) {
            userDao.updateAvatarStage(newStage)
            newStage // Return new stage for celebration
        } else {
            null
        }
    }

    suspend fun updateSkills(
        empathy: Int = 0,
        confidence: Int = 0,
        communication: Int = 0,
        leadership: Int = 0,
        resilience: Int = 0,
    ) {
        userDao.updateSkills(empathy, confidence, communication, leadership, resilience)
    }

    suspend fun checkAndUpdateStreak() {
        val profile = userDao.getUserProfileSync() ?: return
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        if (profile.lastActiveDate == today) return // Already checked in today

        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val newStreak = if (profile.lastActiveDate == yesterday) {
            profile.streakDays + 1
        } else {
            1 // Streak broken, reset
        }
        userDao.updateStreak(newStreak, today)
    }

    suspend fun completeMission(missionId: String, xpEarned: Int) {
        userDao.insertCompletedMission(
            CompletedMission(missionId = missionId, xpEarned = xpEarned)
        )
        userDao.incrementMissions()
    }

    suspend fun getCompletedMissionIdsSince(since: Long): List<String> {
        return userDao.getCompletedMissionIdsSince(since)
    }

    suspend fun saveSession(session: SocialLabSession) {
        userDao.insertSession(session)
        userDao.incrementSessions()
    }
}
