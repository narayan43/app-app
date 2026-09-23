package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders pure white minimalist line-art / outline vector icons
 * matching the aesthetic in the reference screenshots.
 */
@Composable
fun MinimalistLineIcon(
    appName: String,
    packageName: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color = Color.White
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.width
            val strokeW = s * 0.075f
            val strokeStyle = Stroke(
                width = strokeW,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )

            val nameLower = appName.lowercase()
            val pkgLower = packageName.lowercase()

            when {
                // Phone / Dialer
                nameLower.contains("phone") || nameLower.contains("dialer") || pkgLower.contains("dialer") -> {
                    drawPhoneIcon(s, tint, strokeStyle)
                }
                // Messages / Chat / SMS
                nameLower.contains("message") || nameLower.contains("sms") || nameLower.contains("chat") -> {
                    drawMessageIcon(s, tint, strokeStyle)
                }
                // WhatsApp
                nameLower.contains("whatsapp") || pkgLower.contains("whatsapp") -> {
                    drawWhatsAppIcon(s, tint, strokeStyle)
                }
                // Facebook
                nameLower.contains("facebook") || pkgLower.contains("facebook") || pkgLower.contains("katana") -> {
                    drawFacebookIcon(s, tint, strokeStyle)
                }
                // YouTube
                nameLower.contains("youtube") || pkgLower.contains("youtube") -> {
                    drawYouTubeIcon(s, tint, strokeStyle)
                }
                // Chrome / Browser
                nameLower.contains("chrome") || nameLower.contains("browser") || pkgLower.contains("browser") -> {
                    drawChromeIcon(s, tint, strokeStyle)
                }
                // Pinterest
                nameLower.contains("pinterest") || pkgLower.contains("pinterest") -> {
                    drawPinterestIcon(s, tint, strokeStyle)
                }
                // Camera
                nameLower.contains("camera") || pkgLower.contains("camera") -> {
                    drawCameraIcon(s, tint, strokeStyle)
                }
                // Photos / Gallery
                nameLower.contains("photo") || nameLower.contains("gallery") -> {
                    drawGalleryIcon(s, tint, strokeStyle)
                }
                // Settings
                nameLower.contains("setting") -> {
                    drawSettingsIcon(s, tint, strokeStyle)
                }
                // Music / Audio
                nameLower.contains("music") || nameLower.contains("spotify") || nameLower.contains("sound") -> {
                    drawMusicIcon(s, tint, strokeStyle)
                }
                // Maps / Navigation / Find
                nameLower.contains("map") || nameLower.contains("nav") || nameLower.contains("find") -> {
                    drawPinIcon(s, tint, strokeStyle)
                }
                // Calendar
                nameLower.contains("calendar") -> {
                    drawCalendarIcon(s, tint, strokeStyle)
                }
                // Clock / Alarm
                nameLower.contains("clock") || nameLower.contains("alarm") || nameLower.contains("time") -> {
                    drawClockIcon(s, tint, strokeStyle)
                }
                // Calculator
                nameLower.contains("calc") -> {
                    drawCalculatorIcon(s, tint, strokeStyle)
                }
                // Files / Documents
                nameLower.contains("file") || nameLower.contains("document") || nameLower.contains("drive") -> {
                    drawFolderIcon(s, tint, strokeStyle)
                }
                // Voice / Recorder
                nameLower.contains("record") || nameLower.contains("voice") || nameLower.contains("mic") -> {
                    drawMicIcon(s, tint, strokeStyle)
                }
                // Default Minimalist Outline Monogram
                else -> {
                    drawGenericOutlineGlyph(s, appName.take(1).uppercase(), tint, strokeStyle)
                }
            }
        }
    }
}

private fun DrawScope.drawPhoneIcon(s: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(s * 0.32f, s * 0.22f)
        cubicTo(s * 0.25f, s * 0.28f, s * 0.25f, s * 0.45f, s * 0.40f, s * 0.60f)
        cubicTo(s * 0.55f, s * 0.75f, s * 0.72f, s * 0.75f, s * 0.78f, s * 0.68f)
        lineTo(s * 0.68f, s * 0.58f)
        lineTo(s * 0.60f, s * 0.62f)
        cubicTo(s * 0.52f, s * 0.58f, s * 0.42f, s * 0.48f, s * 0.38f, s * 0.40f)
        lineTo(s * 0.42f, s * 0.32f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawMessageIcon(s: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(s * 0.20f, s * 0.28f)
        cubicTo(s * 0.20f, s * 0.22f, s * 0.25f, s * 0.18f, s * 0.32f, s * 0.18f)
        lineTo(s * 0.68f, s * 0.18f)
        cubicTo(s * 0.75f, s * 0.18f, s * 0.80f, s * 0.22f, s * 0.80f, s * 0.28f)
        lineTo(s * 0.80f, s * 0.58f)
        cubicTo(s * 0.80f, s * 0.64f, s * 0.75f, s * 0.68f, s * 0.68f, s * 0.68f)
        lineTo(s * 0.45f, s * 0.68f)
        lineTo(s * 0.30f, s * 0.82f)
        lineTo(s * 0.30f, s * 0.68f)
        lineTo(s * 0.32f, s * 0.68f)
        cubicTo(s * 0.25f, s * 0.68f, s * 0.20f, s * 0.64f, s * 0.20f, s * 0.58f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawWhatsAppIcon(s: Float, color: Color, stroke: Stroke) {
    // Clean circular outline with bubble notch
    val path = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(s * 0.15f, s * 0.15f, s * 0.85f, s * 0.85f))
        moveTo(s * 0.25f, s * 0.75f)
        lineTo(s * 0.15f, s * 0.90f)
        lineTo(s * 0.35f, s * 0.83f)
    }
    drawPath(path, color, style = stroke)
    // Small handset inside
    val handset = Path().apply {
        moveTo(s * 0.38f, s * 0.38f)
        cubicTo(s * 0.38f, s * 0.55f, s * 0.45f, s * 0.62f, s * 0.62f, s * 0.62f)
    }
    drawPath(handset, color, style = stroke)
}

private fun DrawScope.drawFacebookIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.36f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    val fPath = Path().apply {
        moveTo(s * 0.58f, s * 0.32f)
        cubicTo(s * 0.52f, s * 0.32f, s * 0.48f, s * 0.36f, s * 0.48f, s * 0.44f)
        lineTo(s * 0.48f, s * 0.72f)
        moveTo(s * 0.42f, s * 0.48f)
        lineTo(s * 0.56f, s * 0.48f)
    }
    drawPath(fPath, color, style = stroke)
}

private fun DrawScope.drawYouTubeIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.16f, s * 0.24f),
        size = Size(s * 0.68f, s * 0.52f),
        cornerRadius = CornerRadius(s * 0.15f, s * 0.15f),
        style = stroke
    )
    val play = Path().apply {
        moveTo(s * 0.44f, s * 0.38f)
        lineTo(s * 0.60f, s * 0.50f)
        lineTo(s * 0.44f, s * 0.62f)
        close()
    }
    drawPath(play, color, style = stroke)
}

private fun DrawScope.drawChromeIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.36f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    drawCircle(color, radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    drawLine(color, Offset(s * 0.5f, s * 0.35f), Offset(s * 0.85f, s * 0.35f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.63f, s * 0.58f), Offset(s * 0.45f, s * 0.85f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.37f, s * 0.58f), Offset(s * 0.20f, s * 0.40f), strokeWidth = stroke.width)
}

private fun DrawScope.drawPinterestIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.36f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    val pPath = Path().apply {
        moveTo(s * 0.46f, s * 0.32f)
        lineTo(s * 0.42f, s * 0.72f)
        moveTo(s * 0.46f, s * 0.32f)
        cubicTo(s * 0.58f, s * 0.32f, s * 0.60f, s * 0.48f, s * 0.48f, s * 0.50f)
    }
    drawPath(pPath, color, style = stroke)
}

private fun DrawScope.drawCameraIcon(s: Float, color: Color, stroke: Stroke) {
    val body = Path().apply {
        moveTo(s * 0.20f, s * 0.36f)
        lineTo(s * 0.35f, s * 0.36f)
        lineTo(s * 0.40f, s * 0.26f)
        lineTo(s * 0.60f, s * 0.26f)
        lineTo(s * 0.65f, s * 0.36f)
        lineTo(s * 0.80f, s * 0.36f)
        cubicTo(s * 0.84f, s * 0.36f, s * 0.86f, s * 0.38f, s * 0.86f, s * 0.42f)
        lineTo(s * 0.86f, s * 0.72f)
        cubicTo(s * 0.86f, s * 0.76f, s * 0.84f, s * 0.78f, s * 0.80f, s * 0.78f)
        lineTo(s * 0.20f, s * 0.78f)
        cubicTo(s * 0.16f, s * 0.78f, s * 0.14f, s * 0.76f, s * 0.14f, s * 0.72f)
        lineTo(s * 0.14f, s * 0.42f)
        close()
    }
    drawPath(body, color, style = stroke)
    drawCircle(color, radius = s * 0.13f, center = Offset(s * 0.5f, s * 0.56f), style = stroke)
}

private fun DrawScope.drawGalleryIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.18f, s * 0.22f),
        size = Size(s * 0.64f, s * 0.56f),
        cornerRadius = CornerRadius(s * 0.08f, s * 0.08f),
        style = stroke
    )
    val mountain = Path().apply {
        moveTo(s * 0.24f, s * 0.68f)
        lineTo(s * 0.42f, s * 0.45f)
        lineTo(s * 0.54f, s * 0.58f)
        lineTo(s * 0.64f, s * 0.48f)
        lineTo(s * 0.76f, s * 0.68f)
    }
    drawPath(mountain, color, style = stroke)
    drawCircle(color, radius = s * 0.05f, center = Offset(s * 0.64f, s * 0.35f), style = stroke)
}

private fun DrawScope.drawSettingsIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    drawCircle(color, radius = s * 0.32f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    for (i in 0 until 6) {
        val angle = (i * 60f) * (Math.PI / 180f).toFloat()
        val cos = Math.cos(angle.toDouble()).toFloat()
        val sin = Math.sin(angle.toDouble()).toFloat()
        drawLine(
            color = color,
            start = Offset(s * 0.5f + cos * s * 0.32f, s * 0.5f + sin * s * 0.32f),
            end = Offset(s * 0.5f + cos * s * 0.40f, s * 0.5f + sin * s * 0.40f),
            strokeWidth = stroke.width * 1.5f
        )
    }
}

private fun DrawScope.drawMusicIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.09f, center = Offset(s * 0.35f, s * 0.68f), style = stroke)
    drawCircle(color, radius = s * 0.09f, center = Offset(s * 0.68f, s * 0.58f), style = stroke)
    drawLine(color, Offset(s * 0.44f, s * 0.68f), Offset(s * 0.44f, s * 0.28f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.77f, s * 0.58f), Offset(s * 0.77f, s * 0.18f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.44f, s * 0.28f), Offset(s * 0.77f, s * 0.18f), strokeWidth = stroke.width * 1.4f)
}

private fun DrawScope.drawPinIcon(s: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.82f)
        cubicTo(s * 0.3f, s * 0.56f, s * 0.25f, s * 0.44f, s * 0.25f, s * 0.36f)
        cubicTo(s * 0.25f, s * 0.22f, s * 0.36f, s * 0.16f, s * 0.50f, s * 0.16f)
        cubicTo(s * 0.64f, s * 0.16f, s * 0.75f, s * 0.22f, s * 0.75f, s * 0.36f)
        cubicTo(s * 0.75f, s * 0.44f, s * 0.70f, s * 0.56f, s * 0.50f, s * 0.82f)
        close()
    }
    drawPath(path, color, style = stroke)
    drawCircle(color, radius = s * 0.08f, center = Offset(s * 0.5f, s * 0.36f), style = stroke)
}

private fun DrawScope.drawCalendarIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.20f, s * 0.25f),
        size = Size(s * 0.60f, s * 0.55f),
        cornerRadius = CornerRadius(s * 0.08f, s * 0.08f),
        style = stroke
    )
    drawLine(color, Offset(s * 0.20f, s * 0.40f), Offset(s * 0.80f, s * 0.40f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.35f, s * 0.18f), Offset(s * 0.35f, s * 0.25f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.65f, s * 0.18f), Offset(s * 0.65f, s * 0.25f), strokeWidth = stroke.width)
}

private fun DrawScope.drawClockIcon(s: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = s * 0.35f, center = Offset(s * 0.5f, s * 0.5f), style = stroke)
    drawLine(color, Offset(s * 0.5f, s * 0.5f), Offset(s * 0.5f, s * 0.28f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.5f, s * 0.5f), Offset(s * 0.65f, s * 0.5f), strokeWidth = stroke.width)
}

private fun DrawScope.drawCalculatorIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.22f, s * 0.20f),
        size = Size(s * 0.56f, s * 0.60f),
        cornerRadius = CornerRadius(s * 0.08f, s * 0.08f),
        style = stroke
    )
    // Plus / minus / equal signs
    drawLine(color, Offset(s * 0.32f, s * 0.38f), Offset(s * 0.44f, s * 0.38f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.38f, s * 0.32f), Offset(s * 0.38f, s * 0.44f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.56f, s * 0.38f), Offset(s * 0.68f, s * 0.38f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.56f, s * 0.56f), Offset(s * 0.68f, s * 0.56f), strokeWidth = stroke.width)
    drawLine(color, Offset(s * 0.56f, s * 0.64f), Offset(s * 0.68f, s * 0.64f), strokeWidth = stroke.width)
}

private fun DrawScope.drawFolderIcon(s: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(s * 0.18f, s * 0.32f)
        lineTo(s * 0.40f, s * 0.32f)
        lineTo(s * 0.48f, s * 0.40f)
        lineTo(s * 0.82f, s * 0.40f)
        cubicTo(s * 0.84f, s * 0.40f, s * 0.86f, s * 0.42f, s * 0.86f, s * 0.44f)
        lineTo(s * 0.86f, s * 0.72f)
        cubicTo(s * 0.86f, s * 0.74f, s * 0.84f, s * 0.76f, s * 0.82f, s * 0.76f)
        lineTo(s * 0.18f, s * 0.76f)
        cubicTo(s * 0.16f, s * 0.76f, s * 0.14f, s * 0.74f, s * 0.14f, s * 0.72f)
        lineTo(s * 0.14f, s * 0.36f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawMicIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.40f, s * 0.22f),
        size = Size(s * 0.20f, s * 0.38f),
        cornerRadius = CornerRadius(s * 0.10f, s * 0.10f),
        style = stroke
    )
    val holder = Path().apply {
        moveTo(s * 0.30f, s * 0.44f)
        cubicTo(s * 0.30f, s * 0.66f, s * 0.70f, s * 0.66f, s * 0.70f, s * 0.44f)
    }
    drawPath(holder, color, style = stroke)
    drawLine(color, Offset(s * 0.5f, s * 0.66f), Offset(s * 0.5f, s * 0.78f), strokeWidth = stroke.width)
}

private fun DrawScope.drawGenericOutlineGlyph(s: Float, letter: String, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(s * 0.20f, s * 0.20f),
        size = Size(s * 0.60f, s * 0.60f),
        cornerRadius = CornerRadius(s * 0.16f, s * 0.16f),
        style = stroke
    )
    // Draw simple cross-bars inside
    drawLine(
        color = color,
        start = Offset(s * 0.35f, s * 0.5f),
        end = Offset(s * 0.65f, s * 0.5f),
        strokeWidth = stroke.width
    )
}
