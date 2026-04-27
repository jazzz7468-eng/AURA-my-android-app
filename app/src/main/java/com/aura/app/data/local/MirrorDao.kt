package com.aura.app.data.local

import androidx.room.*
import com.aura.app.data.local.entities.MirrorSession
import kotlinx.coroutines.flow.Flow

@Dao
interface MirrorDao {

    @Insert
    suspend fun insertSession(session: MirrorSession)

    @Query("SELECT * FROM mirror_sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<MirrorSession>>

    @Query("SELECT * FROM mirror_sessions ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<MirrorSession>>

    @Query("SELECT COUNT(*) FROM mirror_sessions")
    suspend fun getSessionCount(): Int

    @Query("SELECT AVG(overallConfidence) FROM mirror_sessions")
    suspend fun getAverageConfidence(): Float?

    @Query("SELECT AVG(postureScore) FROM mirror_sessions WHERE completedAt > :since")
    suspend fun getAveragePostureSince(since: Long): Float?
}
