package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeMode
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferences by viewModel.preferences.collectAsState()
            val isDark = when (preferences.themeMode) {
                ThemeMode.LIGHT -> false
                else -> true
            }

            // Back button behavior for Home Launcher:
            // If search is active or dialogs are open, close them;
            // Otherwise, don't exit the launcher.
            val isSearchActive by viewModel.isSearchActive.collectAsState()
            val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
            val selectedApp by viewModel.selectedAppForOptions.collectAsState()

            BackHandler(enabled = isSearchActive || isSettingsOpen || selectedApp != null) {
                when {
                    selectedApp != null -> viewModel.closeAppOptions()
                    isSettingsOpen -> viewModel.closeSettings()
                    isSearchActive -> viewModel.toggleSearch(false)
                }
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = when (preferences.themeMode) {
                        ThemeMode.OLED_BLACK -> Color(0xFF000000)
                        ThemeMode.CHARCOAL_DARK -> Color(0xFF101012)
                        ThemeMode.LIGHT -> Color(0xFFF6F6F6)
                        ThemeMode.SYSTEM -> Color(0xFF000000)
                    }
                ) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // When Home button is pressed while on the home screen, close search and sheets
        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.toggleSearch(false)
            viewModel.closeSettings()
            viewModel.closeAppOptions()
        }
    }
}
