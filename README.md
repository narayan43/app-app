# Flow Launcher 🌊
### Minimalist Android Home Launcher with Fluid Liquid Alphabet Interaction

**Flow Launcher** is a modern, high-performance Android application built with **Jetpack Compose**, **Kotlin Coroutines**, and **Material Design 3**. It offers a distraction-free home screen experience featuring an interactive, fluid liquid alphabet rail with dynamic wave distortion, haptic feedback, OLED dark mode, and instant application search.

---

## 📲 How to Download the App (APK)

You have three easy ways to download and run the APK on your Android device:

### Option 1: Download from GitHub Releases / Actions (Automated Builds)
Every time code is pushed or triggered, GitHub automatically compiles the latest version of the app:

1. Open this repository on GitHub (`https://github.com/narayan43/Fluid-launcher-app` or your repository fork).
2. **From GitHub Releases (Recommended - Direct APK download):**
   - Click on **Releases** on the right side of the repository.
   - Under the latest release **Assets**, click on the `.apk` file (e.g., `fluid-launcher-app-debug-build-X.apk`) to download directly to your Android device.
3. **From GitHub Actions Artifacts:**
   - Click on the **Actions** tab at the top of the repository.
   - Click the most recent workflow run (titled **"Build Android APK"**).
   - Scroll down to the bottom of the page to the **Artifacts** section.
   - Click **`app-debug-apk`** to download the archive containing `app-debug.apk`.
   - Transfer and install `app-debug.apk` onto your Android phone!

> **Tip:** You can also trigger a fresh build at any time by going to **Actions** ➔ **Build Android APK** ➔ click **Run workflow**.

---

### Option 2: Direct Download from Google AI Studio
If you are working in Google AI Studio:

1. In the top toolbar, click the **Settings / More Options** menu (three dots or gear icon).
2. Look for **Export / Download**.
3. Select **Download APK**. The pre-compiled APK (`app-debug.apk`) will download directly to your computer or phone.

---

### Option 3: Build Locally via Command Line / Android Studio
If you want to compile the project locally on your machine:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/narayan43/Fluid-launcher-app.git
   cd Fluid-launcher-app
   ```

2. **Build the Debug APK:**
   ```bash
   # Using Gradle 9.3+ and Java 17:
   gradle :app:assembleDebug
   ```

3. **Locate your APK:** The compiled APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Install onto a connected Android device:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📥 How to Install the APK on Your Android Device

1. Once downloaded, open the `app-debug.apk` file from your device's **Downloads** folder or notification shade.
2. If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*:
   - Tap **Settings**.
   - Enable **Allow from this source**.
3. Tap **Install** and wait for installation to complete.
4. Tap **Open** to launch **Flow Launcher**!
5. When prompted by Android, choose **Flow Launcher** and select **"Always"** to set it as your default home launcher.

---

## 🚀 Key Features

- 🌊 **Fluid Liquid Alphabet Rail:**
  - Dynamic wave distortion curve that smoothly follows your finger touch along the alphabet rail.
  - Interactive letter preview bubble with fluid animation physics.
  - Configurable rail positioning (Left or Right side) and adjustable fluid wave intensity (Subtle, Balanced, Dynamic).

- ⚡ **Tactile Haptic Feedback:**
  - Micro-vibrations generated dynamically as your finger glides through each letter.
  - Native Android Vibrator / VibrationEffect support with fallback compatibility.

- 🔍 **Instant App Search & Launch:**
  - Fast, responsive search filtering across all installed applications.
  - Quick launch with keyboard auto-focus and smooth transitions.

- 🎨 **Minimalist & OLED Aesthetic:**
  - Pure OLED true black mode (`#000000`) for battery savings and high contrast on modern AMOLED screens.
  - Light mode, Dark mode, and System Default theme support.
  - Clean typography using Material 3 styling.

- ⚙️ **Customization & Default Launcher Controls:**
  - Intuitive settings panel to customize alphabet side, accent colors, wave physics, and haptics.
  - Built-in shortcut to open system Default Home App settings.

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** MVVM (Model-View-ViewModel) with StateFlow & Coroutines
- **Build System:** Gradle 9.3 (Kotlin DSL) with Version Catalog (`libs.versions.toml`)
- **Android Gradle Plugin (AGP):** 9.1.1
- **Target SDK:** Android 36
- **Min SDK:** Android 24 (Nougat)
- **CI/CD:** Automated GitHub Actions with direct APK publishing to GitHub Releases

---

## 🔄 Syncing Changes to GitHub

To push future updates from Google AI Studio to your GitHub repository:

1. In the top-right corner of Google AI Studio, click the **Export / GitHub** button.
2. Select **Push to GitHub** (or select the `Fluid-launcher-app` repository).
3. Confirm the push. Your GitHub repository will instantly be updated with the latest code, and the GitHub Actions workflow will automatically compile a fresh APK!
