package com.aura.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mirror_sessions")
data class MirrorSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationSeconds: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),

    // Posture & Body Language Scores (0-100)
    val postureScore: Int = 0,
    val eyeContactScore: Int = 0,
    val gestureScore: Int = 0,
    val overallConfidence: Int = 0,

    // Feedback
    val postureFeedback: String = "",
    val eyeContactFeedback: String = "",
    val gestureFeedback: String = "",

    // XP
    val xpEarned: Int = 30,
)
