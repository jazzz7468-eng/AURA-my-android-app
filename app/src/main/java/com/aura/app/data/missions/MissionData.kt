package com.aura.app.data.missions

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val type: MissionType,
    val xpReward: Int,
    val icon: String,
    val targetCount: Int = 1,
    val skillFocus: String? = null,
)

enum class MissionType { DAILY, CHALLENGE, QUICK }

object MissionData {

    val dailyMissions = listOf(
        Mission("daily_1", "Social Practice", "Complete 1 Social Lab session", MissionType.DAILY, 50, "🧪"),
        Mission("daily_2", "New Territory", "Try a scenario you haven't done before", MissionType.DAILY, 75, "🌟"),
        Mission("daily_3", "Triple Crown", "Complete all 3 daily missions", MissionType.DAILY, 100, "👑"),
    )

    val challenges = listOf(
        Mission("challenge_empathy", "Empathy Sprint", "Complete 3 empathy-focused scenarios", MissionType.CHALLENGE, 150, "💛", targetCount = 3, skillFocus = "empathy"),
        Mission("challenge_high_score", "High Achiever", "Score 80+ in any scenario", MissionType.CHALLENGE, 100, "🏆", skillFocus = "confidence"),
        Mission("challenge_streak_7", "Week Warrior", "Maintain a 7-day streak", MissionType.CHALLENGE, 200, "🔥", targetCount = 7),
        Mission("challenge_all_scenarios", "Explorer", "Try all 5 scenarios for your age group", MissionType.CHALLENGE, 250, "🗺️", targetCount = 5),
        Mission("challenge_streak_30", "Monthly Master", "Maintain a 30-day streak", MissionType.CHALLENGE, 500, "⚡", targetCount = 30),
    )

    val quickTasks = listOf(
        Mission("quick_spin", "Spin the Wheel", "Answer a random skill quiz question", MissionType.QUICK, 25, "🎡"),
        Mission("quick_reflect", "Daily Reflection", "What's one thing you're proud of today?", MissionType.QUICK, 30, "📝"),
        Mission("quick_kindness", "Kindness Log", "Record a kind act you did today", MissionType.QUICK, 40, "💝"),
    )
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val skill: String,
)

object QuizData {
    val questions = listOf(
        // Empathy
        QuizQuestion(
            question = "Your friend is crying. What should you do first?",
            options = listOf("Tell them to stop crying", "Ask what's wrong and listen", "Walk away", "Tell a joke"),
            correctIndex = 1,
            explanation = "Listening first shows you care about their feelings.",
            skill = "empathy",
        ),
        QuizQuestion(
            question = "Someone shares a problem with you. What's the best response?",
            options = listOf("\"That's not a big deal\"", "\"I told you so\"", "\"That sounds really tough. I'm here for you.\"", "Change the subject"),
            correctIndex = 2,
            explanation = "Validating feelings builds trust and connection.",
            skill = "empathy",
        ),
        QuizQuestion(
            question = "You see a new student eating alone. What would show empathy?",
            options = listOf("Ignore them", "Talk about them with friends", "Invite them to sit with you", "Stare at them"),
            correctIndex = 2,
            explanation = "Including others shows you understand how loneliness feels.",
            skill = "empathy",
        ),

        // Confidence
        QuizQuestion(
            question = "You need to present in front of class. What helps build confidence?",
            options = listOf("Skip the presentation", "Practice multiple times beforehand", "Wing it without preparation", "Read directly from notes"),
            correctIndex = 1,
            explanation = "Preparation is the foundation of confidence.",
            skill = "confidence",
        ),
        QuizQuestion(
            question = "Someone criticizes your work unfairly. How should you respond?",
            options = listOf("Get angry and yell", "Calmly ask for specific feedback", "Never share your work again", "Agree with everything they say"),
            correctIndex = 1,
            explanation = "Confident people seek to understand criticism, not avoid it.",
            skill = "confidence",
        ),
        QuizQuestion(
            question = "What body language shows confidence?",
            options = listOf("Crossed arms, looking down", "Slouching, avoiding eye contact", "Standing tall, making eye contact", "Fidgeting constantly"),
            correctIndex = 2,
            explanation = "Open posture and eye contact project confidence.",
            skill = "confidence",
        ),

        // Communication
        QuizQuestion(
            question = "During a disagreement, what's the best way to express your viewpoint?",
            options = listOf("\"You're wrong!\"", "\"I think...\" followed by your reasoning", "Stay silent", "\"Whatever\""),
            correctIndex = 1,
            explanation = "I-statements are respectful and clear.",
            skill = "communication",
        ),
        QuizQuestion(
            question = "What is active listening?",
            options = listOf("Waiting for your turn to talk", "Fully focusing on and understanding the speaker", "Nodding while checking your phone", "Interrupting with advice"),
            correctIndex = 1,
            explanation = "Active listening means giving full attention and trying to understand.",
            skill = "communication",
        ),
        QuizQuestion(
            question = "You don't understand someone's instructions. What should you do?",
            options = listOf("Pretend you understand", "Ask clarifying questions", "Ignore the instructions", "Complain to someone else"),
            correctIndex = 1,
            explanation = "Asking questions shows maturity and prevents mistakes.",
            skill = "communication",
        ),

        // Leadership
        QuizQuestion(
            question = "A team member is struggling with their task. What should a good leader do?",
            options = listOf("Do the task yourself", "Blame them for being slow", "Offer help and guidance", "Ignore it"),
            correctIndex = 2,
            explanation = "Leaders empower others rather than doing everything themselves.",
            skill = "leadership",
        ),
        QuizQuestion(
            question = "Your team disagrees on an approach. What's the best leadership move?",
            options = listOf("Force your opinion", "Let everyone argue it out", "Facilitate a discussion where all views are heard", "Avoid the conflict"),
            correctIndex = 2,
            explanation = "Great leaders ensure every voice is heard before deciding.",
            skill = "leadership",
        ),

        // Resilience
        QuizQuestion(
            question = "You failed a test you studied hard for. What's the resilient response?",
            options = listOf("Give up on the subject", "Blame the teacher", "Analyze what went wrong and adjust your study method", "Pretend it doesn't matter"),
            correctIndex = 2,
            explanation = "Resilience means learning from failure and adapting.",
            skill = "resilience",
        ),
        QuizQuestion(
            question = "Someone spreads a rumor about you. What shows resilience?",
            options = listOf("Spread rumors about them", "Let it consume you with worry", "Address it calmly with the person and move on", "Isolate yourself"),
            correctIndex = 2,
            explanation = "Resilient people address issues directly without escalating.",
            skill = "resilience",
        ),
    )

    fun getRandomQuestion(): QuizQuestion = questions.random()
}
