package com.aura.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val ageGroup: String = "adults",       // "kids", "teens", "adults"
    val xp: Int = 0,
    val level: Int = 1,
    val avatarStage: Int = 1,              // 1-5
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val hasOnboarded: Boolean = false,

    // Skills (0-100 each)
    val empathy: Int = 0,
    val confidence: Int = 0,
    val communication: Int = 0,
    val leadership: Int = 0,
    val resilience: Int = 0,

    // Stats
    val totalSessions: Int = 0,
    val totalMissionsCompleted: Int = 0,
)

@Entity(tableName = "completed_missions")
data class CompletedMission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val missionId: String,
    val completedAt: Long = System.currentTimeMillis(),
    val xpEarned: Int,
)

@Entity(tableName = "social_lab_sessions")
data class SocialLabSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scenarioId: String,
    val scenarioTitle: String = "",
    val overallScore: Int = 0,
    val empathyScore: Int = 0,
    val confidenceScore: Int = 0,
    val communicationScore: Int = 0,
    val leadershipScore: Int = 0,
    val resilienceScore: Int = 0,
    val xpEarned: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),
)
