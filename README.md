<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Language"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/AI-Gemini%20API-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white" alt="Gemini AI"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-brightgreen?style=for-the-badge" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License"/>
</p>

<h1 align="center">✨ AURA</h1>
<h3 align="center"><i>Adaptive Understanding & Reflective Advancement</i></h3>

<p align="center">
  <b>The first lifelong coaching platform that masters soft skills and emotional intelligence through AI-powered, age-adaptive experiences.</b>
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-project-structure">Project Structure</a> •
  <a href="#-screenshots">Screenshots</a> •
  <a href="#-roadmap">Roadmap</a> •
  <a href="#-contributing">Contributing</a>
</p>

---

## 🌟 What is AURA?

**AURA** is an AI-driven Android application designed to help users develop **soft skills** and **emotional intelligence** through personalized, age-adaptive coaching. Unlike traditional self-help apps, AURA combines cutting-edge AI (Google Gemini), computer vision (ML Kit), and gamification mechanics into a single, beautifully crafted experience.

Whether you're a teenager learning to navigate social situations or an adult refining leadership skills — AURA adapts its coaching style, content, and visual design to your age, personality, and growth journey.

---

## 🚀 Features

### 🧠 Social Lab — AI-Powered Roleplay Engine
Practice real-world social scenarios through interactive AI conversations powered by **Google Gemini API**. Get nuanced feedback on communication style, empathy, assertiveness, and conflict resolution.

- 🎭 Diverse scenario library (workplace, interviews, relationships, public speaking, etc.)
- 💬 Real-time NLP-powered chat with context-aware AI personas
- 📊 Post-session performance analysis & soft skill scoring

### 🪞 Mirror-Tech — Computer Vision Body Language Coach
Use your device's camera for real-time **body language analysis** powered by **ML Kit Pose Detection**.

- 📐 Posture confidence scoring (shoulder alignment, head position)
- 🤲 Gesture openness detection (arm crossing, hand placement)
- 🧍 Stance stability analysis
- 📈 Session history with progress tracking

### 📓 AI Emotion Journal
A guided journaling experience with **AI-powered emotional analysis** via Gemini.

- 🎯 Mood detection & emotional pattern recognition
- 💡 Personalized growth suggestions based on journal entries
- 📅 Calendar view for tracking emotional patterns over time
- 🔄 Gemini-powered reflection prompts

### 🏆 Daily Missions & XP Gamification
Stay engaged with a gamified progression system.

- ⚡ Daily mission cards with varied challenge types
- 🎮 XP-based leveling system with milestone celebrations
- 🔥 Streak tracking to build consistent habits
- 🏅 Stage-up celebrations with dynamic animations

### 🌱 Living AURA Avatar
A visual representation of your growth that **evolves** as you progress.

- 🎨 Dynamic aura visualization that changes with your level
- 📊 Radar chart showing skill distribution across dimensions
- 🎬 Stage-up evolution timeline with celebration animations
- ✨ Animated particle effects reflecting your current state

### 🎨 Age-Adaptive Design System
Two distinct UI themes that automatically adapt:

- **Kids Mode** (13–17): Playful, vibrant, gamified interface with rounded elements
- **Adult Mode** (18+): Sleek, professional, minimal design with sophisticated aesthetics

### 📲 Home Screen Widget & Notifications
Stay connected to your growth journey even outside the app.

- 🔔 Daily check-in notifications via **WorkManager**
- 📱 Compose-based home screen widget using **Jetpack Glance**

### 🛡️ Deep Onboarding Assessment
Personalized onboarding that tailors the entire experience to you.

- 📝 Personality assessment questionnaire
- 🎂 Age-based content & design adaptation
- 🎯 Goal setting for personalized mission generation

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **DI** | Dagger Hilt |
| **Database** | Room (SQLite) |
| **Preferences** | DataStore |
| **Networking** | Retrofit + OkHttp + Gson |
| **AI / LLM** | Google Gemini API |
| **Computer Vision** | ML Kit Pose Detection |
| **Camera** | CameraX |
| **Image Loading** | Coil |
| **Navigation** | Navigation Compose |
| **Background Work** | WorkManager |
| **Home Widget** | Jetpack Glance |
| **Permissions** | Accompanist Permissions |
| **Build System** | Gradle (Kotlin DSL) + KSP |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |

---

## 🏗️ Architecture

AURA follows a clean **MVVM architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│  ┌───────────┐  ┌───────────┐  ┌──────────────┐ │
│  │  Screens  │  │Components │  │    Theme     │ │
│  │(Compose)  │  │(Reusable) │  │ (Adaptive)   │ │
│  └─────┬─────┘  └─────┬─────┘  └──────────────┘ │
│        │              │                          │
│  ┌─────┴──────────────┴─────┐                    │
│  │       ViewModels         │                    │
│  │  (State + Business Logic)│                    │
│  └─────────────┬────────────┘                    │
├────────────────┼────────────────────────────────-┤
│           Data Layer                             │
│  ┌─────────────┴────────────┐                    │
│  │      Repositories        │                    │
│  └──┬──────────────────┬────┘                    │
│  ┌──┴───┐          ┌───┴────┐                    │
│  │Local │          │ Remote │                    │
│  │(Room)│          │(Gemini)│                    │
│  └──────┘          └────────┘                    │
├──────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌────────────────┐ │
│  │   Hilt   │  │   Worker  │  │    Widget     │ │
│  │   (DI)   │  │(WorkMgr) │  │  (Glance)     │ │
│  └──────────┘  └──────────┘  └────────────────┘ │
└──────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
AURA/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/aura/app/
│       │   ├── AuraApplication.kt          # Hilt Application class
│       │   ├── MainActivity.kt             # Single-activity entry point
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AuraDatabase.kt     # Room database definition
│       │   │   │   ├── JournalDao.kt       # Journal data access
│       │   │   │   ├── MirrorDao.kt        # Mirror session data access
│       │   │   │   ├── UserDao.kt          # User profile data access
│       │   │   │   └── entities/
│       │   │   │       ├── Entities.kt     # Core database entities
│       │   │   │       ├── JournalEntry.kt # Journal entry model
│       │   │   │       └── MirrorSession.kt# Mirror session model
│       │   │   ├── mirror/
│       │   │   │   └── PoseAnalyzer.kt     # ML Kit pose analysis logic
│       │   │   ├── missions/
│       │   │   │   └── MissionData.kt      # Mission definitions & data
│       │   │   ├── remote/
│       │   │   │   └── GeminiApiService.kt # Gemini API integration
│       │   │   ├── repository/
│       │   │   │   ├── JournalRepository.kt# Journal business logic
│       │   │   │   └── UserRepository.kt   # User management logic
│       │   │   └── scenarios/
│       │   │       └── ScenarioData.kt     # Social Lab scenario library
│       │   ├── di/
│       │   │   └── AppModule.kt            # Hilt dependency modules
│       │   ├── navigation/
│       │   │   └── AuraNavGraph.kt         # Compose navigation graph
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── AdaptiveCard.kt     # Age-adaptive card component
│       │   │   │   ├── AuraBottomBar.kt    # Custom bottom navigation
│       │   │   │   ├── XPProgressBar.kt    # Animated XP progress bar
│       │   │   │   └── avatar/
│       │   │   │       ├── EvolutionTimeline.kt  # Avatar evolution timeline
│       │   │   │       ├── LivingAura.kt         # Animated aura visualization
│       │   │   │       ├── RadarChart.kt          # Skill radar chart
│       │   │   │       └── StageUpCelebration.kt  # Level-up animations
│       │   │   ├── screens/
│       │   │   │   ├── avatar/AvatarScreen.kt      # Avatar & progress view
│       │   │   │   ├── dashboard/DashboardScreen.kt# Main dashboard
│       │   │   │   ├── journal/JournalScreen.kt    # AI emotion journal
│       │   │   │   ├── mirror/MirrorScreen.kt      # Body language coach
│       │   │   │   ├── missions/MissionsScreen.kt  # Daily missions hub
│       │   │   │   ├── onboarding/OnboardingScreen.kt # Onboarding flow
│       │   │   │   └── sociallab/
│       │   │   │       ├── ChatScreen.kt           # AI roleplay chat
│       │   │   │       └── SocialLabScreen.kt      # Scenario selection
│       │   │   └── theme/
│       │   │       ├── AuraThemes.kt       # Age-adaptive theme system
│       │   │       ├── Color.kt            # Color definitions
│       │   │       ├── Shape.kt            # Shape tokens
│       │   │       ├── Theme.kt            # Material 3 theme config
│       │   │       └── Type.kt             # Typography definitions
│       │   ├── util/
│       │   │   └── XPManager.kt            # XP/leveling calculations
│       │   ├── viewmodel/
│       │   │   ├── AvatarViewModel.kt      # Avatar state management
│       │   │   ├── JournalViewModel.kt     # Journal state management
│       │   │   ├── MirrorViewModel.kt      # Mirror session management
│       │   │   ├── MissionsViewModel.kt    # Missions state management
│       │   │   ├── SocialLabViewModel.kt   # Social Lab state management
│       │   │   └── UserViewModel.kt        # User profile management
│       │   ├── widget/
│       │   │   ├── AuraWidget.kt           # Glance widget UI
│       │   │   └── AuraWidgetReceiver.kt   # Widget broadcast receiver
│       │   └── worker/
│       │       └── DailyCheckInWorker.kt   # Daily notification worker
│       └── res/                            # Resources (layouts, drawables, etc.)
├── gradle/wrapper/
├── build.gradle.kts                        # Root build config
├── settings.gradle.kts                     # Project settings
├── gradle.properties                       # Gradle properties
└── index.html                              # Landing page
```

---

## 🏁 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** or higher
- **Android SDK 34**
- A **Google Gemini API Key** ([Get one here](https://aistudio.google.com/app/apikey))

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/jazzz7468-eng/AURA-my-android-app.git
   cd AURA-my-android-app
   ```

2. **Add your Gemini API Key**
   
   Open `app/build.gradle.kts` and replace the API key in the `buildConfigField`:
   ```kotlin
   buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_API_KEY_HERE\"")
   ```

   > ⚠️ **Important**: Never commit API keys to version control. Consider using `local.properties` or environment variables for production use.

3. **Sync & Build**
   
   Open the project in Android Studio, let Gradle sync, then:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run**
   
   Deploy to an emulator or physical device running **Android 8.0 (API 26)** or higher.

### Permissions Required

| Permission | Purpose |
|-----------|---------|
| `CAMERA` | Mirror-Tech body language analysis |
| `POST_NOTIFICATIONS` | Daily check-in reminders |
| `INTERNET` | Gemini AI API communication |

---

## 📸 Screenshots

> 🚧 *Screenshots coming soon! The app features a stunning dark-mode-first design with vibrant gradient accents and fluid animations.*

---

## 🗺️ Roadmap

- [x] Core MVVM architecture with Hilt DI
- [x] Social Lab — AI roleplay with Gemini
- [x] Mirror-Tech — Pose detection body language analysis
- [x] AI Emotion Journal with mood tracking
- [x] Gamified missions & XP progression system
- [x] Living AURA avatar with evolution timeline
- [x] Age-adaptive theming (Kids vs Adult mode)
- [x] Deep onboarding personality assessment
- [x] Home screen widget (Jetpack Glance)
- [x] Daily notification system (WorkManager)
- [ ] Cloud sync & multi-device support
- [ ] Social features (peer challenges, group activities)
- [ ] Advanced analytics dashboard
- [ ] Wearable integration (Wear OS)
- [ ] Offline AI capabilities
- [ ] Multi-language support

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

Please ensure your code follows the existing architecture patterns and includes appropriate documentation.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👩‍💻 Author

**Janvi Gehlot**

- GitHub: [@jazzz7468-eng](https://github.com/jazzz7468-eng)

---

<p align="center">
  <b>Built with ❤️ and Kotlin</b><br/>
  <i>"Your aura is the silent introduction your presence makes before you even speak."</i>
</p>
