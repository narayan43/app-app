package com.example

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InstalledAppsRepository
import com.example.data.PreferencesRepository
import com.example.fluid.FluidState
import com.example.fluid.HapticController
import com.example.model.AlphabetPosition
import com.example.model.AppItem
import com.example.model.FluidIntensity
import com.example.model.LauncherPreferences
import com.example.model.PaperTearStyle
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)
    private val appsRepository = InstalledAppsRepository(application, viewModelScope)
    private val hapticController = HapticController(application)

    val preferences: StateFlow<LauncherPreferences> = preferencesRepository.preferences
    val installedApps: StateFlow<List<AppItem>> = appsRepository.installedApps
    val isLoadingApps: StateFlow<Boolean> = appsRepository.isLoading

    val fluidState = FluidState()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _selectedAppForOptions = MutableStateFlow<AppItem?>(null)
    val selectedAppForOptions: StateFlow<AppItem?> = _selectedAppForOptions.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isOnboardingOpen = MutableStateFlow(!preferences.value.hasSeenOnboarding)
    val isOnboardingOpen: StateFlow<Boolean> = _isOnboardingOpen.asStateFlow()

    private var previousChar: Char? = null

    // Currently selected letter filter ('*' = pinned favorites only, null/'*' = default home, 'A'..'Z' = apps starting with that letter)
    private val _activeLetterFilter = MutableStateFlow<Char?>('*')
    val activeLetterFilter: StateFlow<Char?> = _activeLetterFilter.asStateFlow()

    // Combined filtered list of apps
    val filteredApps: StateFlow<List<AppItem>> = combine(
        installedApps,
        _searchQuery,
        _activeLetterFilter,
        preferences
    ) { apps, query, letterFilter, prefs ->
        val queryTrimmed = query.trim().lowercase()
        val mapped = apps.map { app ->
            app.copy(isPinned = prefs.pinnedPackages.contains(app.packageName))
        }

        if (queryTrimmed.isNotEmpty()) {
            mapped.filter {
                it.label.lowercase().contains(queryTrimmed) ||
                        it.packageName.lowercase().contains(queryTrimmed)
            }
        } else if (letterFilter == null || letterFilter == '*') {
            // Show all installed apps ordered alphabetically so the user sees everything immediately
            mapped
        } else if (letterFilter == '#') {
            mapped.filter { !it.firstChar.isLetter() }
        } else {
            // Only show apps starting with that selected letter
            mapped.filter { it.firstChar.equals(letterFilter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Pinned apps at the top
    val pinnedApps: StateFlow<List<AppItem>> = combine(
        installedApps,
        preferences
    ) { apps, prefs ->
        apps.filter { prefs.pinnedPackages.contains(it.packageName) }
            .map { it.copy(isPinned = true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Target letter selected for scrolling
    private val _targetScrollChar = MutableStateFlow<Char?>(null)
    val targetScrollChar: StateFlow<Char?> = _targetScrollChar.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun openAppOptions(app: AppItem) {
        _selectedAppForOptions.value = app
    }

    fun closeAppOptions() {
        _selectedAppForOptions.value = null
    }

    fun dismissOnboarding() {
        _isOnboardingOpen.value = false
        preferencesRepository.setHasSeenOnboarding(true)
    }

    fun launchApp(app: AppItem) {
        appsRepository.launchApp(app)
    }

    fun openAppDetails(packageName: String) {
        appsRepository.openAppDetails(packageName)
    }

    fun uninstallApp(packageName: String) {
        appsRepository.requestUninstall(packageName)
    }

    fun togglePinnedApp(packageName: String) {
        preferencesRepository.togglePinnedApp(packageName)
    }

    fun setThemeMode(mode: ThemeMode) = preferencesRepository.setThemeMode(mode)
    fun setAlphabetPosition(position: AlphabetPosition) = preferencesRepository.setAlphabetPosition(position)
    fun setFluidIntensity(intensity: FluidIntensity) = preferencesRepository.setFluidIntensity(intensity)
    fun setFluidColor(hex: Long) = preferencesRepository.setFluidColor(hex)
    fun setHapticFeedback(enabled: Boolean) = preferencesRepository.setHapticFeedback(enabled)
    fun setShowClock(show: Boolean) = preferencesRepository.setShowClock(show)
    fun setShowDate(show: Boolean) = preferencesRepository.setShowDate(show)
    fun setUse24HourFormat(use24Hour: Boolean) = preferencesRepository.setUse24HourFormat(use24Hour)
    fun setMonochromeIcons(enabled: Boolean) = preferencesRepository.setMonochromeIcons(enabled)
    fun setPaperTearStyle(style: PaperTearStyle) = preferencesRepository.setPaperTearStyle(style)

    fun onGestureDown(x: Float, y: Float) {
        fluidState.touchX = x
        fluidState.touchY = y
        fluidState.smoothedY = y
        fluidState.velocityY = 0f
        fluidState.smoothedVelocityY = 0f
        fluidState.previousMoveDy = 0f
        fluidState.springBackDisplacement = 0f
    }

    fun onGestureActivate(x: Float, y: Float, totalHeight: Float) {
        fluidState.isActive = true
        onGestureMove(x, y, 0f, totalHeight)
    }

    fun onGestureMove(
        x: Float,
        y: Float,
        dy: Float,
        totalHeight: Float
    ) {
        fluidState.touchX = x
        fluidState.touchY = y

        // Direction reversal detection:
        // When finger changes direction rapidly, apply an instantaneous spring-back compensation
        // and a responsive tracking factor so the fluid curve immediately snaps and does NOT lag or stick.
        val prevDy = fluidState.previousMoveDy
        val isDirectionReversal = (dy > 1.5f && prevDy < -1.5f) || (dy < -1.5f && prevDy > 1.5f)

        if (isDirectionReversal) {
            // Apply spring-back multiplier: quickly reset lingering opposite momentum
            // and boost convergence towards the new finger position
            fluidState.smoothedVelocityY = dy * 35f
            fluidState.springBackDisplacement = -prevDy * 0.45f
            // Snap smoothedY closer immediately (no sticking to old position)
            fluidState.smoothedY += (y - fluidState.smoothedY) * 0.85f
        } else {
            // Decay spring-back displacement smoothly
            fluidState.springBackDisplacement *= 0.65f
            // Fast, fluid convergence
            fluidState.smoothedY += (y - fluidState.smoothedY) * 0.60f
        }

        if (dy != 0f) {
            fluidState.previousMoveDy = dy
        }

        // Velocity tracking and exponential smoothing with resistance
        val currentVel = dy * 60f
        fluidState.smoothedVelocityY += (currentVel - fluidState.smoothedVelocityY) * 0.45f
        fluidState.velocityY = currentVel

        // Wobble phase update
        fluidState.wobblePhase += 0.15f

        // Shorter in height slide bar: occupies ~54% of the screen height, vertically centered
        val railHeight = totalHeight * 0.54f
        val railTop = (totalHeight - railHeight) / 2f + (totalHeight * 0.05f)

        val effectiveY = fluidState.smoothedY + fluidState.springBackDisplacement
        val normalized = ((effectiveY - railTop) / railHeight).coerceIn(0f, 1f)
        val maxIndex = (FluidState.ITEMS.size - 1).toFloat()
        val index = normalized * maxIndex

        fluidState.selectedIndex = index

        val nearestInt = index.toInt().coerceIn(0, FluidState.ITEMS.size - 1)
        val selectedItem = FluidState.ITEMS[nearestInt]
        fluidState.activeItem = selectedItem

        // Haptic feedback when crossing letter boundary
        val selectedChar = if (selectedItem == "★") '*' else if (selectedItem == "•") '#' else selectedItem.first()
        if (selectedChar != previousChar) {
            previousChar = selectedChar
            hapticController.triggerLetterTick(preferences.value.hapticFeedbackEnabled)
            _activeLetterFilter.value = selectedChar
            _targetScrollChar.value = selectedChar
        }
    }

    fun onGestureUp() {
        fluidState.isActive = false
        previousChar = null
        fluidState.previousMoveDy = 0f
        fluidState.springBackDisplacement = 0f
        // Notice: _activeLetterFilter stays preserved so apps for that letter remain displayed when finger is released!
    }

    fun selectLetterFilter(char: Char?) {
        _activeLetterFilter.value = char
    }

    fun clearTargetScrollChar() {
        _targetScrollChar.value = null
    }

    fun requestDefaultLauncher(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                if (roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return
                }
            }
            // Fallback for older Android or devices without RoleManager
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        appsRepository.unregister()
    }
}
