package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainViewModel
import com.example.model.AlphabetPosition
import com.example.model.AppItem
import com.example.model.ThemeMode
import com.example.ui.components.AppListItem
import com.example.ui.components.AppOptionsBottomSheet
import com.example.ui.components.FluidAlphabetOverlay
import com.example.ui.components.OnboardingSheet
import com.example.ui.components.PaperTearHeader
import com.example.ui.components.SettingsSheet
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val pinnedApps by viewModel.pinnedApps.collectAsStateWithLifecycle()
    val isLoadingApps by viewModel.isLoadingApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val selectedAppForOptions by viewModel.selectedAppForOptions.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isOnboardingOpen by viewModel.isOnboardingOpen.collectAsStateWithLifecycle()
    val targetScrollChar by viewModel.targetScrollChar.collectAsStateWithLifecycle()
    val activeLetterFilter by viewModel.activeLetterFilter.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    // Determine if Flow Launcher is current default home
    val isDefaultLauncher = remember(context) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo?.activityInfo?.packageName == context.packageName
        } catch (_: Exception) {
            false
        }
    }

    // Precalculate letter-to-index mapping for smooth fluid scrolling
    val letterIndexMap = remember(filteredApps, pinnedApps) {
        val map = mutableMapOf<Char, Int>()
        var currentIndex = 0

        if (pinnedApps.isNotEmpty()) {
            currentIndex += 1 // Header
            currentIndex += pinnedApps.size
        }

        var lastChar: Char? = null
        for (i in filteredApps.indices) {
            val app = filteredApps[i]
            if (app.firstChar != lastChar) {
                lastChar = app.firstChar
                map[app.firstChar] = currentIndex
                currentIndex += 1 // Section header item
            }
            currentIndex += 1
        }
        map
    }

    // Scroll to letter when fluid alphabet changes
    LaunchedEffect(targetScrollChar) {
        targetScrollChar?.let { char ->
            when (char) {
                '*' -> listState.scrollToItem(0)
                '#' -> {
                    val count = listState.layoutInfo.totalItemsCount
                    if (count > 0) listState.scrollToItem(count - 1)
                }
                else -> {
                    val index = letterIndexMap[char]
                        ?: letterIndexMap.entries.firstOrNull { it.key >= char }?.value
                        ?: letterIndexMap.values.lastOrNull()

                    if (index != null) {
                        listState.scrollToItem(index)
                    }
                }
            }
        }
    }

    val backgroundColor = when (preferences.themeMode) {
        ThemeMode.OLED_BLACK -> Color(0xFF000000)
        ThemeMode.CHARCOAL_DARK -> Color(0xFF101012)
        ThemeMode.LIGHT -> Color(0xFFF6F6F6)
        ThemeMode.SYSTEM -> Color(0xFF000000)
    }

    val fluidColor = Color(preferences.fluidColorHex)
    val dragThresholdPx = with(density) { 10.dp.toPx() }
    var headerHeightPx by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .testTag("home_screen_root")
    ) {
        val screenHeight = constraints.maxHeight.toFloat()
        val screenWidth = constraints.maxWidth.toFloat()

        // Full-screen diagonal paper-tear wallpaper (true OLED pitch black top-left, slate-blue torn paper bottom-right)
        if (preferences.paperTearStyle == com.example.model.PaperTearStyle.PAPER_TEAR) {
            com.example.ui.components.DiagonalPaperTearBackground(
                modifier = Modifier.fillMaxSize()
            )
        }

        // Detect vertical drag anywhere on the screen for the liquid alphabet
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(screenWidth, screenHeight) {
                    var totalDy = 0f
                    var startX = 0f
                    var startY = 0f
                    var isFluidActive = false
                    val rightEdgeZone = screenWidth - with(density) { 55.dp.toPx() }

                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            startY = offset.y
                            totalDy = 0f
                            isFluidActive = false

                            // If touch starts below the header area, activate tracking
                            if (offset.y > headerHeightPx) {
                                viewModel.onGestureDown(offset.x, offset.y)
                                // If touch starts right on the alphabet slider column, activate immediately!
                                if (offset.x >= rightEdgeZone) {
                                    isFluidActive = true
                                    viewModel.onGestureActivate(offset.x, offset.y, screenHeight)
                                }
                            }
                        },
                        onDragEnd = {
                            if (isFluidActive) {
                                viewModel.onGestureUp()
                            }
                            isFluidActive = false
                        },
                        onDragCancel = {
                            if (isFluidActive) {
                                viewModel.onGestureUp()
                            }
                            isFluidActive = false
                        },
                        onDrag = { change: PointerInputChange, dragAmount ->
                            // Never intercept gestures starting in the header (settings, search, home badge)
                            if (startY <= headerHeightPx) {
                                return@detectDragGestures
                            }

                            totalDy += dragAmount.y

                            if (!isFluidActive) {
                                // Activate if vertical gesture exceeds threshold or finger is near the slider strip
                                if (abs(totalDy) > dragThresholdPx || change.position.x >= rightEdgeZone) {
                                    isFluidActive = true
                                    viewModel.onGestureActivate(startX, change.position.y, screenHeight)
                                    change.consume()
                                }
                            } else {
                                viewModel.onGestureMove(
                                    x = change.position.x,
                                    y = change.position.y,
                                    dy = dragAmount.y,
                                    totalHeight = screenHeight
                                )
                                change.consume()
                            }
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Paper Tear Header with Clock & Search
                PaperTearHeader(
                    showClock = preferences.showClock,
                    showDate = preferences.showDate,
                    paperTearStyle = preferences.paperTearStyle,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    isSearchActive = isSearchActive,
                    onToggleSearch = { viewModel.toggleSearch(it) },
                    onOpenSettings = { viewModel.openSettings() },
                    isDefaultLauncher = isDefaultLauncher,
                    onSetDefaultLauncher = { viewModel.requestDefaultLauncher(context) },
                    activeLetterFilter = activeLetterFilter,
                    onResetToAllApps = { viewModel.selectLetterFilter('*') },
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        headerHeightPx = coordinates.size.height.toFloat()
                    }
                )

                // App List View
                if (isLoadingApps) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = fluidColor,
                            modifier = Modifier.testTag("apps_loading_indicator")
                        )
                    }
                } else if (filteredApps.isEmpty() && pinnedApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No apps matching \"$searchQuery\""
                            } else if (activeLetterFilter != null && activeLetterFilter != '*') {
                                "No apps starting with '$activeLetterFilter'"
                            } else {
                                "No pinned apps. Slide the A-Z bar on the right to browse apps."
                            },
                            color = Color(0xFF888888),
                            fontSize = 15.sp,
                            modifier = Modifier.testTag("empty_apps_label")
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("apps_lazy_list")
                    ) {
                        // Pinned Favorites section (if any)
                        if (pinnedApps.isNotEmpty() && searchQuery.isEmpty()) {
                            item(key = "header_favorites") {
                                Text(
                                    text = "FAVORITES",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = fluidColor,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                                )
                            }
                            itemsIndexed(
                                items = pinnedApps,
                                key = { _, app -> "home_pinned_${app.packageName}" }
                            ) { _, app ->
                                AppListItem(
                                    app = app,
                                    isMonochrome = preferences.monochromeIcons,
                                    onClick = { viewModel.launchApp(app) },
                                    onLongClick = { viewModel.openAppOptions(app) },
                                    modifier = Modifier.testTag("app_item_${app.packageName}")
                                )
                            }
                        }

                        // Section header when a specific letter is filtered
                        if (searchQuery.isEmpty() && activeLetterFilter != null && activeLetterFilter != '*') {
                            item(key = "header_selected_letter") {
                                Text(
                                    text = activeLetterFilter.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        } else if (pinnedApps.isNotEmpty() && searchQuery.isEmpty() && filteredApps.isNotEmpty()) {
                            item(key = "header_all_apps") {
                                Text(
                                    text = "ALL APPS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF888888),
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // Display all apps (or search / letter filtered list)
                        for (i in filteredApps.indices) {
                            val app = filteredApps[i]
                            item(key = "app_${app.packageName}") {
                                AppListItem(
                                    app = app,
                                    isMonochrome = preferences.monochromeIcons,
                                    onClick = { viewModel.launchApp(app) },
                                    onLongClick = { viewModel.openAppOptions(app) },
                                    modifier = Modifier.testTag("app_item_${app.packageName}")
                                )
                            }
                        }

                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }

            // The Liquid Alphabet Overlay (renders the fluid physics & alphabet letters)
            FluidAlphabetOverlay(
                fluidState = viewModel.fluidState,
                alphabetPosition = preferences.alphabetPosition,
                fluidIntensity = preferences.fluidIntensity,
                fluidColor = fluidColor,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fluid_alphabet_overlay")
            )
        }
    }

    // App Options Bottom Sheet
    selectedAppForOptions?.let { app ->
        AppOptionsBottomSheet(
            app = app,
            isPinned = preferences.pinnedPackages.contains(app.packageName),
            onDismiss = { viewModel.closeAppOptions() },
            onTogglePin = { viewModel.togglePinnedApp(app.packageName) },
            onOpenAppInfo = { viewModel.openAppDetails(app.packageName) },
            onUninstall = { viewModel.uninstallApp(app.packageName) }
        )
    }

    // Settings Bottom Sheet
    if (isSettingsOpen) {
        SettingsSheet(
            preferences = preferences,
            onSetThemeMode = { viewModel.setThemeMode(it) },
            onSetAlphabetPosition = { viewModel.setAlphabetPosition(it) },
            onSetFluidIntensity = { viewModel.setFluidIntensity(it) },
            onSetFluidColor = { viewModel.setFluidColor(it) },
            onSetHaptics = { viewModel.setHapticFeedback(it) },
            onSetShowClock = { viewModel.setShowClock(it) },
            onSetShowDate = { viewModel.setShowDate(it) },
            onSetMonochromeIcons = { viewModel.setMonochromeIcons(it) },
            onSetPaperTearStyle = { viewModel.setPaperTearStyle(it) },
            onSetDefaultLauncher = {
                viewModel.requestDefaultLauncher(context)
                viewModel.closeSettings()
            },
            onDismiss = { viewModel.closeSettings() }
        )
    }

    // Onboarding Bottom Sheet
    if (isOnboardingOpen) {
        OnboardingSheet(
            onDismiss = { viewModel.dismissOnboarding() },
            onSetDefaultLauncher = {
                viewModel.requestDefaultLauncher(context)
                viewModel.dismissOnboarding()
            }
        )
    }
}
