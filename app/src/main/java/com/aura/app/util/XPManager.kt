package com.aura.app.util

object XPManager {
    // 1 Level = 100 XP. Infinite scaling.
    fun getStageForXP(xp: Int): Int = (xp / 100) + 1

    fun getXPForNextStage(currentXP: Int): Int {
        val currentLevel = getLevelForXP(currentXP)
        return currentLevel * 100
    }

    fun getXPProgressToNextStage(currentXP: Int): Float {
        return (currentXP % 100).toFloat() / 100f
    }

    fun getLevelForXP(xp: Int): Int = (xp / 100) + 1

    fun getStageName(stage: Int): String {
        // Dynamic titles that scale up to level 100+
        return when {
            stage >= 100 -> "Ascendant Master"
            stage >= 80 -> "Aura Sovereign"
            stage >= 60 -> "Visionary"
            stage >= 40 -> "Luminary"
            stage >= 20 -> "Radiant"
            stage >= 10 -> "Bloom"
            stage >= 5 -> "Sprout"
            else -> "Seed"
        }
    }

    fun getStageEmoji(stage: Int): String {
        return when {
            stage >= 100 -> "🌌"
            stage >= 80 -> "👑"
            stage >= 60 -> "🔱"
            stage >= 40 -> "✨"
            stage >= 20 -> "🌸"
            stage >= 10 -> "🌿"
            else -> "🌱"
        }
    }

    fun getStageDescription(stage: Int, ageGroup: String): String {
        return "You have reached Level $stage! Your skills are resonating at a higher frequency. Keep training to unlock deeper visual complexity in your Aura."
    }
}
