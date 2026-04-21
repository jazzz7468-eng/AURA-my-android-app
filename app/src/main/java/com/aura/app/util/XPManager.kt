package com.aura.app.util

object XPManager {
    private val STAGE_THRESHOLDS = listOf(0, 200, 500, 1000, 2000)

    fun getStageForXP(xp: Int): Int = when {
        xp >= 2000 -> 5
        xp >= 1000 -> 4
        xp >= 500 -> 3
        xp >= 200 -> 2
        else -> 1
    }

    fun getXPForNextStage(currentXP: Int): Int {
        return STAGE_THRESHOLDS.firstOrNull { it > currentXP } ?: 2000
    }

    fun getXPProgressToNextStage(currentXP: Int): Float {
        val currentStage = getStageForXP(currentXP)
        if (currentStage == 5) return 1f

        val currentThreshold = STAGE_THRESHOLDS[currentStage - 1]
        val nextThreshold = STAGE_THRESHOLDS[currentStage]
        return (currentXP - currentThreshold).toFloat() / (nextThreshold - currentThreshold).toFloat()
    }

    fun getLevelForXP(xp: Int): Int = (xp / 100) + 1

    fun getStageName(stage: Int): String = when (stage) {
        1 -> "Seed"
        2 -> "Sprout"
        3 -> "Bloom"
        4 -> "Radiant"
        5 -> "Ascended"
        else -> "Unknown"
    }

    fun getStageEmoji(stage: Int): String = when (stage) {
        1 -> "🌱"
        2 -> "🌿"
        3 -> "🌸"
        4 -> "✨"
        5 -> "👑"
        else -> "❓"
    }

    fun getStageDescription(stage: Int, ageGroup: String): String = when (ageGroup) {
        "kids" -> when (stage) {
            1 -> "You're just starting your adventure!"
            2 -> "You're growing and learning new things!"
            3 -> "Wow, you're becoming really confident!"
            4 -> "You're shining bright like a star!"
            5 -> "You're a true HERO! Amazing!"
            else -> ""
        }
        "teens" -> when (stage) {
            1 -> "Every journey starts with a single step."
            2 -> "You're finding your voice. Keep going!"
            3 -> "Your skills are really starting to show."
            4 -> "You're radiating confidence and growth."
            5 -> "Legendary status. You've mastered the game."
            else -> ""
        }
        else -> when (stage) {
            1 -> "Beginning your professional growth journey."
            2 -> "Building foundational soft skills."
            3 -> "Developing a strong professional presence."
            4 -> "Your leadership presence is undeniable."
            5 -> "Visionary. You inspire those around you."
            else -> ""
        }
    }
}
