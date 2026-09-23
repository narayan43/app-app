package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingSheet(
    onDismiss: () -> Unit,
    onSetDefaultLauncher: () -> Unit
) {
    // Normal full-screen composable instead of ModalBottomSheet.
    // Prevents window inflation crashes on ColorOS / OEM launchers on first frame.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE6000000))
            .testTag("onboarding_sheet"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF101012),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262628)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Meet Flow Launcher",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A minimalist home screen with liquid navigation.",
                    fontSize = 14.sp,
                    color = Color(0xFF8E8E93)
                )

                Spacer(modifier = Modifier.height(24.dp))

                FeatureItem(
                    icon = Icons.Rounded.Gesture,
                    title = "Fluid Alphabet Gesture",
                    description = "Swipe vertically anywhere on screen. Letters emerge dynamically from the water wave."
                )

                Spacer(modifier = Modifier.height(16.dp))

                FeatureItem(
                    icon = Icons.Rounded.PushPin,
                    title = "Pin Favorites",
                    description = "Long press any app to pin it right beneath the clock for instant access."
                )

                Spacer(modifier = Modifier.height(16.dp))

                FeatureItem(
                    icon = Icons.Rounded.Home,
                    title = "Default Home",
                    description = "Set as default launcher to replace your home screen and return here on Home press."
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        onSetDefaultLauncher()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_set_default_button")
                ) {
                    Text(
                        text = "Set as Default Launcher",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_continue_button")
                ) {
                    Text(
                        text = "Explore Launcher",
                        color = Color(0xFFCCCCCC),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0x2200E5FF),
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier
                    .padding(8.dp)
                    .size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF9E9EA2),
                lineHeight = 18.sp
            )
        }
    }
}
