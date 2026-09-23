package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlphabetPosition
import com.example.model.FluidColorPreset
import com.example.model.FluidIntensity
import com.example.model.LauncherPreferences
import com.example.model.PaperTearStyle
import com.example.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    preferences: LauncherPreferences,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetAlphabetPosition: (AlphabetPosition) -> Unit,
    onSetFluidIntensity: (FluidIntensity) -> Unit,
    onSetFluidColor: (Long) -> Unit,
    onSetHaptics: (Boolean) -> Unit,
    onSetShowClock: (Boolean) -> Unit,
    onSetShowDate: (Boolean) -> Unit,
    onSetMonochromeIcons: (Boolean) -> Unit,
    onSetPaperTearStyle: (PaperTearStyle) -> Unit,
    onSetDefaultLauncher: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF000000),
        dragHandle = {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFF262628),
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("settings_sheet")
        ) {
            Text(
                text = "Launcher Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Button: Set as Default Launcher
            Button(
                onClick = onSetDefaultLauncher,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("set_as_default_launcher_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Set as Default Home App",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF222226))
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Header Style
            SettingsSectionHeader(title = "Header Style (Paper Tear Accent)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaperTearStyle.values().forEach { style ->
                    val isSelected = preferences.paperTearStyle == style
                    val label = when (style) {
                        PaperTearStyle.PAPER_TEAR -> "Paper Tear"
                        PaperTearStyle.MINIMAL_WAVE -> "Minimal Wave"
                        PaperTearStyle.CLEAN_FLAT -> "Clean Flat"
                    }
                    SelectablePill(
                        label = label,
                        selected = isSelected,
                        onClick = { onSetPaperTearStyle(style) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Alphabet Rail Position
            SettingsSectionHeader(title = "Alphabet Rail Side")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectablePill(
                    label = "Right Side",
                    selected = preferences.alphabetPosition == AlphabetPosition.RIGHT,
                    onClick = { onSetAlphabetPosition(AlphabetPosition.RIGHT) },
                    modifier = Modifier.weight(1f)
                )
                SelectablePill(
                    label = "Left Side",
                    selected = preferences.alphabetPosition == AlphabetPosition.LEFT,
                    onClick = { onSetAlphabetPosition(AlphabetPosition.LEFT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Fluid Wave Intensity
            SettingsSectionHeader(title = "Fluid Wave Intensity")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FluidIntensity.values().forEach { intensity ->
                    val isSelected = preferences.fluidIntensity == intensity
                    val label = when (intensity) {
                        FluidIntensity.LOW -> "Subtle"
                        FluidIntensity.MEDIUM -> "Balanced"
                        FluidIntensity.HIGH -> "Dynamic"
                    }
                    SelectablePill(
                        label = label,
                        selected = isSelected,
                        onClick = { onSetFluidIntensity(intensity) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Fluid Color Preset
            SettingsSectionHeader(title = "Fluid Color Tint")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FluidColorPreset.values().forEach { preset ->
                    val isSelected = preferences.fluidColorHex == preset.hex
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(preset.hex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSetFluidColor(preset.hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = if (preset.hex == 0xFFE0E0E0L) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Theme
            SettingsSectionHeader(title = "Theme")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectablePill(
                    label = "OLED Black",
                    selected = preferences.themeMode == ThemeMode.OLED_BLACK,
                    onClick = { onSetThemeMode(ThemeMode.OLED_BLACK) },
                    modifier = Modifier.weight(1f)
                )
                SelectablePill(
                    label = "Charcoal",
                    selected = preferences.themeMode == ThemeMode.CHARCOAL_DARK,
                    onClick = { onSetThemeMode(ThemeMode.CHARCOAL_DARK) },
                    modifier = Modifier.weight(1f)
                )
                SelectablePill(
                    label = "Light",
                    selected = preferences.themeMode == ThemeMode.LIGHT,
                    onClick = { onSetThemeMode(ThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF222226))
            Spacer(modifier = Modifier.height(12.dp))

            // Toggles
            ToggleRow(
                title = "Haptic Letter Ticks",
                description = "Tactile vibration when crossing letters",
                checked = preferences.hapticFeedbackEnabled,
                onCheckedChange = onSetHaptics
            )

            ToggleRow(
                title = "Monochrome Icons",
                description = "Desaturates app icons for pure minimalism",
                checked = preferences.monochromeIcons,
                onCheckedChange = onSetMonochromeIcons
            )

            ToggleRow(
                title = "Show Clock",
                description = "Digital clock in header",
                checked = preferences.showClock,
                onCheckedChange = onSetShowClock
            )

            ToggleRow(
                title = "Show Date",
                description = "Day and date banner",
                checked = preferences.showDate,
                onCheckedChange = onSetShowDate
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF222226))
            Spacer(modifier = Modifier.height(16.dp))

            // App Updates Info Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF101012),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222226)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Flow Launcher",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "v1.0 (Build 1)",
                            fontSize = 12.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seamless Direct In-Place Updates: When you update this app, install the new APK directly over this version. Android automatically preserves all your pinned apps, custom preferences, and settings without needing to uninstall.",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9EA3),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF8E8E93),
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SelectablePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color(0xFF18181A) else Color(0xFF08080A),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, Color.White) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E1E22)),
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) Color.White else Color(0xFF88888C)
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF77777A)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color.White,
                uncheckedThumbColor = Color(0xFF888888),
                uncheckedTrackColor = Color(0xFF1E1E22)
            )
        )
    }
}
