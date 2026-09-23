package com.example.model

enum class ThemeMode {
    OLED_BLACK,
    CHARCOAL_DARK,
    LIGHT,
    SYSTEM
}

enum class AlphabetPosition {
    RIGHT,
    LEFT
}

enum class FluidIntensity {
    LOW,
    MEDIUM,
    HIGH
}

enum class PaperTearStyle {
    CLEAN_FLAT,
    PAPER_TEAR,
    MINIMAL_WAVE
}

enum class FluidColorPreset(val title: String, val hex: Long) {
    OPAL_WHITE("Opal White", 0xFFFFFFFF),
    AQUA_CYAN("Aqua Cyan", 0xFF00E5FF),
    ELECTRIC_BLUE("Electric Blue", 0xFF2979FF),
    VIOLET_AURA("Violet Aura", 0xFFB388FF),
    EMERALD_MINT("Emerald Mint", 0xFF00E676),
    SUNSET_CORAL("Sunset Coral", 0xFFFF6E40)
}

data class LauncherPreferences(
    val themeMode: ThemeMode = ThemeMode.OLED_BLACK,
    val alphabetPosition: AlphabetPosition = AlphabetPosition.RIGHT,
    val fluidIntensity: FluidIntensity = FluidIntensity.MEDIUM,
    val fluidColorHex: Long = 0xFFFFFFFF,
    val hapticFeedbackEnabled: Boolean = true,
    val showClock: Boolean = true,
    val showDate: Boolean = true,
    val use24HourFormat: Boolean = false,
    val monochromeIcons: Boolean = true,
    val paperTearStyle: PaperTearStyle = PaperTearStyle.CLEAN_FLAT,
    val pinnedPackages: Set<String> = emptySet(),
    val hasSeenOnboarding: Boolean = false
)
