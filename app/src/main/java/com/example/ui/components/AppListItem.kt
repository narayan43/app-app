package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem

private val GrayScaleMatrix = ColorMatrix().apply {
    setToSaturation(0f)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppItem,
    isMonochrome: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon: Clean Minimalist Line Art Icon or Monochrome App Icon
        if (isMonochrome) {
            MinimalistLineIcon(
                appName = app.label,
                packageName = app.packageName,
                size = 28.dp,
                tint = Color.White
            )
        } else if (app.icon != null) {
            Image(
                bitmap = app.icon,
                contentDescription = "${app.label} icon",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        } else {
            MinimalistLineIcon(
                appName = app.label,
                packageName = app.packageName,
                size = 28.dp,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        // App Label: Clean minimalist white typography
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.2.sp
            ),
            color = Color(0xFFF0F0F0),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Pinned icon indicator
        if (app.isPinned) {
            Icon(
                imageVector = Icons.Rounded.PushPin,
                contentDescription = "Pinned",
                tint = Color(0xFF888888),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
