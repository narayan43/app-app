package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AlphabetPosition
import com.example.model.FluidIntensity
import com.example.model.LauncherPreferences
import com.example.model.PaperTearStyle
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("flow_launcher_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadFromPrefs())
    val preferences: StateFlow<LauncherPreferences> = _preferences.asStateFlow()

    private fun loadFromPrefs(): LauncherPreferences {
        val themeModeStr = prefs.getString(KEY_THEME, ThemeMode.OLED_BLACK.name) ?: ThemeMode.OLED_BLACK.name
        val themeMode = try { ThemeMode.valueOf(themeModeStr) } catch (_: Exception) { ThemeMode.OLED_BLACK }

        val alphabetPosStr = prefs.getString(KEY_ALPHABET_POS, AlphabetPosition.RIGHT.name) ?: AlphabetPosition.RIGHT.name
        val alphabetPos = try { AlphabetPosition.valueOf(alphabetPosStr) } catch (_: Exception) { AlphabetPosition.RIGHT }

        val fluidIntensityStr = prefs.getString(KEY_FLUID_INTENSITY, FluidIntensity.MEDIUM.name) ?: FluidIntensity.MEDIUM.name
        val fluidIntensity = try { FluidIntensity.valueOf(fluidIntensityStr) } catch (_: Exception) { FluidIntensity.MEDIUM }

        val paperTearStr = prefs.getString(KEY_PAPER_TEAR, PaperTearStyle.CLEAN_FLAT.name) ?: PaperTearStyle.CLEAN_FLAT.name
        val paperTearStyle = try { PaperTearStyle.valueOf(paperTearStr) } catch (_: Exception) { PaperTearStyle.CLEAN_FLAT }

        val fluidColor = prefs.getLong(KEY_FLUID_COLOR, 0xFFFFFFFF)
        val haptics = prefs.getBoolean(KEY_HAPTICS, true)
        val showClock = prefs.getBoolean(KEY_SHOW_CLOCK, true)
        val showDate = prefs.getBoolean(KEY_SHOW_DATE, true)
        val use24Hour = prefs.getBoolean(KEY_USE_24_HOUR, false)
        val monochrome = prefs.getBoolean(KEY_MONOCHROME, true)
        val pinned = prefs.getStringSet(KEY_PINNED, emptySet()) ?: emptySet()
        val onboarding = prefs.getBoolean(KEY_ONBOARDING, false)

        return LauncherPreferences(
            themeMode = themeMode,
            alphabetPosition = alphabetPos,
            fluidIntensity = fluidIntensity,
            fluidColorHex = fluidColor,
            hapticFeedbackEnabled = haptics,
            showClock = showClock,
            showDate = showDate,
            use24HourFormat = use24Hour,
            monochromeIcons = monochrome,
            paperTearStyle = paperTearStyle,
            pinnedPackages = pinned,
            hasSeenOnboarding = onboarding
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    fun setAlphabetPosition(position: AlphabetPosition) {
        prefs.edit().putString(KEY_ALPHABET_POS, position.name).apply()
        _preferences.value = _preferences.value.copy(alphabetPosition = position)
    }

    fun setFluidIntensity(intensity: FluidIntensity) {
        prefs.edit().putString(KEY_FLUID_INTENSITY, intensity.name).apply()
        _preferences.value = _preferences.value.copy(fluidIntensity = intensity)
    }

    fun setFluidColor(colorHex: Long) {
        prefs.edit().putLong(KEY_FLUID_COLOR, colorHex).apply()
        _preferences.value = _preferences.value.copy(fluidColorHex = colorHex)
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _preferences.value = _preferences.value.copy(hapticFeedbackEnabled = enabled)
    }

    fun setShowClock(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CLOCK, show).apply()
        _preferences.value = _preferences.value.copy(showClock = show)
    }

    fun setShowDate(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_DATE, show).apply()
        _preferences.value = _preferences.value.copy(showDate = show)
    }

    fun setUse24HourFormat(use24Hour: Boolean) {
        prefs.edit().putBoolean(KEY_USE_24_HOUR, use24Hour).apply()
        _preferences.value = _preferences.value.copy(use24HourFormat = use24Hour)
    }

    fun setMonochromeIcons(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MONOCHROME, enabled).apply()
        _preferences.value = _preferences.value.copy(monochromeIcons = enabled)
    }

    fun setPaperTearStyle(style: PaperTearStyle) {
        prefs.edit().putString(KEY_PAPER_TEAR, style.name).apply()
        _preferences.value = _preferences.value.copy(paperTearStyle = style)
    }

    fun togglePinnedApp(packageName: String) {
        val current = _preferences.value.pinnedPackages.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_PINNED, current).apply()
        _preferences.value = _preferences.value.copy(pinnedPackages = current)
    }

    fun setHasSeenOnboarding(seen: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING, seen).apply()
        _preferences.value = _preferences.value.copy(hasSeenOnboarding = seen)
    }

    companion object {
        private const val KEY_THEME = "theme_mode"
        private const val KEY_ALPHABET_POS = "alphabet_position"
        private const val KEY_FLUID_INTENSITY = "fluid_intensity"
        private const val KEY_FLUID_COLOR = "fluid_color"
        private const val KEY_HAPTICS = "haptic_feedback"
        private const val KEY_SHOW_CLOCK = "show_clock"
        private const val KEY_SHOW_DATE = "show_date"
        private const val KEY_USE_24_HOUR = "use_24_hour_format"
        private const val KEY_MONOCHROME = "monochrome_icons"
        private const val KEY_PAPER_TEAR = "paper_tear_style"
        private const val KEY_PINNED = "pinned_packages"
        private const val KEY_ONBOARDING = "has_seen_onboarding"
    }
}
