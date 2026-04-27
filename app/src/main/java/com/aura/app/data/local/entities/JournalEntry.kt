package com.aura.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rawText: String,
    val createdAt: Long = System.currentTimeMillis(),

    // Sentiment analysis results (filled by Gemini)
    val primaryMood: String = "neutral",       // happy, sad, anxious, excited, calm, angry, neutral
    val moodEmoji: String = "😐",
    val moodScore: Int = 50,                   // 0 (very negative) to 100 (very positive)
    val energyLevel: String = "medium",        // low, medium, high
    val burnoutScore: Int = 0,                 // 0-100

    // AI insight
    val aiInsight: String = "",

    // Tags extracted by AI
    val tags: String = "",                     // Comma-separated: "work,stress,growth"

    // XP earned for this entry
    val xpEarned: Int = 20,
)
