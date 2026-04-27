# 🌟 AURA: AI-Powered Soft Skills Coaching App

**AURA** is a modern, native Android application designed to help users of all ages develop their soft skills and emotional intelligence through interactive, AI-driven roleplay and a gamified personality progression system.

Built with **Kotlin** and **Jetpack Compose**, AURA is fully equipped with an adaptive UI, a local database, and deep integrations with the **Google Gemini API**.

---

## ✨ Core Features

### 1. Age-Adaptive Theming (Material3)
The app features three distinct visual identities that automatically apply based on the user's age demographic:
*   **Kids**: Bright, playful colors, highly rounded corners, and *Nunito* typography.
*   **Teens**: Sleek dark mode, neon cyber-accents, and *Space Grotesk* typography.
*   **Adults**: Professional deep navy aesthetics, subtle glassmorphism, and *Inter* typography.

### 2. The "Living Aura" (Procedural Avatars)
AURA replaces static 2D avatars with a dynamic, code-driven visual engine using Jetpack Compose `Canvas`. Your "Aura" breathes, rotates, and evolves through 5 distinct stages as you earn XP.
*   **Organic Blooms** for Kids.
*   **Kinetic Neon Sparks** for Teens.
*   **Fluid Orbital Flows** for Adults.

### 3. Social Lab (Powered by Gemini)
Practice real-world scenarios in a safe environment.
*   15 unique, age-specific scenarios (e.g., sharing toys for kids, job interviews for adults).
*   The AI acts out the scenario and then provides a detailed **Scorecard** evaluating your Empathy, Confidence, Communication, Leadership, and Resilience.

### 4. Gamified Missions
*   Complete daily tasks and reflective quizzes (Spin-the-Wheel).
*   Track usage streaks and level up to evolve your Aura.

---

## 🛠️ Tech Stack & Architecture

AURA is built following Google's modern Android development best practices.

*   **Language**: Kotlin
*   **UI Toolkit**: Jetpack Compose
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Dependency Injection**: Dagger Hilt
*   **Local Database**: Room (SQLite)
*   **Network & API**: Retrofit, OkHttp, Gson
*   **LLM Integration**: Google Gemini API (`gemini-1.5-flash`)
*   **Build System**: Gradle (Kotlin DSL)

---

## 🚀 Setup & Installation

To run this project on your local machine, you will need Android Studio and a Gemini API key.

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/YOUR-USERNAME/AURA.git
    ```
2.  **Open in Android Studio**:
    Open the `AURA` folder inside Android Studio (Ladybug or newer). Wait for Gradle to finish syncing.
3.  **Add your Gemini API Key**:
    Get a free API key from [Google AI Studio](https://aistudio.google.com/app/apikey).
    Open `app/build.gradle.kts` and replace the placeholder with your actual key:
    ```kotlin
    buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_API_KEY_HERE\"")
    ```
4.  **Run the App**:
    Connect a physical Android device (with USB debugging enabled) or start an Android Virtual Device (Emulator), then click the green **Run** button.

---

## 📸 Screenshots
*(Add screenshots of the Dashboard, Social Lab, and Living Aura here once you have run the app!)*

---

*AURA was created during a 3-day rapid prototyping sprint focusing on bleeding-edge native Android UI/UX and LLM integration.*
