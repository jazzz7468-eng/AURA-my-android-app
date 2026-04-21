package com.aura.app.data.local

import androidx.room.*
import com.aura.app.data.local.entities.CompletedMission
import com.aura.app.data.local.entities.SocialLabSession
import com.aura.app.data.local.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // ── User Profile ──
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET xp = xp + :amount WHERE id = 1")
    suspend fun addXP(amount: Int)

    @Query("UPDATE user_profile SET avatarStage = :stage WHERE id = 1")
    suspend fun updateAvatarStage(stage: Int)

    @Query("UPDATE user_profile SET level = :level WHERE id = 1")
    suspend fun updateLevel(level: Int)

    @Query("UPDATE user_profile SET streakDays = :streak, lastActiveDate = :date WHERE id = 1")
    suspend fun updateStreak(streak: Int, date: String)

    @Query("""
        UPDATE user_profile SET 
            empathy = MIN(100, empathy + :empathy),
            confidence = MIN(100, confidence + :confidence),
            communication = MIN(100, communication + :communication),
            leadership = MIN(100, leadership + :leadership),
            resilience = MIN(100, resilience + :resilience)
        WHERE id = 1
    """)
    suspend fun updateSkills(
        empathy: Int = 0,
        confidence: Int = 0,
        communication: Int = 0,
        leadership: Int = 0,
        resilience: Int = 0,
    )

    @Query("UPDATE user_profile SET totalSessions = totalSessions + 1 WHERE id = 1")
    suspend fun incrementSessions()

    @Query("UPDATE user_profile SET totalMissionsCompleted = totalMissionsCompleted + 1 WHERE id = 1")
    suspend fun incrementMissions()

    // ── Completed Missions ──
    @Insert
    suspend fun insertCompletedMission(mission: CompletedMission)

    @Query("SELECT * FROM completed_missions ORDER BY completedAt DESC")
    fun getCompletedMissions(): Flow<List<CompletedMission>>

    @Query("SELECT missionId FROM completed_missions WHERE completedAt > :since")
    suspend fun getCompletedMissionIdsSince(since: Long): List<String>

    // ── Social Lab Sessions ──
    @Insert
    suspend fun insertSession(session: SocialLabSession)

    @Query("SELECT * FROM social_lab_sessions ORDER BY completedAt DESC")
    fun getSessions(): Flow<List<SocialLabSession>>

    @Query("SELECT COUNT(*) FROM social_lab_sessions")
    suspend fun getSessionCount(): Int
}
