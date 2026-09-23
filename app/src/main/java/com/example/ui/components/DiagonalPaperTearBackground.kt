package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Full-screen diagonal paper-tear wallpaper matching the user's reference screenshots.
 * - Top-left is 100% pure OLED black (#000000) so AMOLED display pixels remain turned completely off.
 * - Bottom-right has a soft slate-blue paper texture (#9cb1c0 / #b2c3d1).
 * - Separated by a diagonal ripped-paper deckled fiber edge with realistic drop shadow.
 */
@Composable
fun DiagonalPaperTearBackground(
    modifier: Modifier = Modifier,
    paperColor: Color = Color(0xFFA6BAC8)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Fill base screen with pure OLED pitch black (pixels completely off)
        drawRect(color = Color(0xFF000000))

        // 2. Generate diagonal jagged paper-tear path
        // Starts around bottom-left (x=0, y=h*0.75f) and cuts diagonally up to right (x=w, y=h*0.38f)
        val startY = h * 0.76f
        val endY = h * 0.36f

        val steps = 48
        val pathPaper = Path()
        val pathEdgeHighlight = Path()
        val pathShadow = Path()

        // Procedural pseudo-random offsets for paper fiber deckled edge
        val jitterOffsets = floatArrayOf(
            -3.5f, 2.0f, -5.0f, 3.2f, -1.8f, 4.5f, -6.0f, 1.2f, 4.0f, -3.2f,
            -6.8f, 2.5f, -1.2f, 3.8f, -4.2f, 5.5f, -3.0f, 0.5f, 3.2f, -4.8f,
            2.2f, -5.8f, 3.0f, -2.2f, 4.2f, -4.8f, 1.5f, -3.8f, 5.2f, -1.8f,
            -5.5f, 2.8f, -1.5f, 4.2f, -3.2f, 2.0f, -4.8f, 3.8f, -2.0f, 0.8f,
            -3.2f, 4.0f, -2.5f, 1.8f, -4.0f, 3.5f, -1.2f, 2.5f
        )

        // Calculate points along the diagonal
        val edgePoints = mutableListOf<Offset>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = t * w
            val baseY = startY + t * (endY - startY)
            val jitter = jitterOffsets[i % jitterOffsets.size]
            edgePoints.add(Offset(x, baseY + jitter))
        }

        // Build Paper polygon (covers lower right under the diagonal)
        pathPaper.moveTo(edgePoints.first().x, edgePoints.first().y)
        for (i in 1 until edgePoints.size) {
            pathPaper.lineTo(edgePoints[i].x, edgePoints[i].y)
        }
        pathPaper.lineTo(w, h)
        pathPaper.lineTo(0f, h)
        pathPaper.close()

        // Build Drop Shadow polygon just underneath the torn edge
        pathShadow.moveTo(edgePoints.first().x, edgePoints.first().y + 8f)
        for (i in 1 until edgePoints.size) {
            pathShadow.lineTo(edgePoints[i].x, edgePoints[i].y + 8f)
        }
        pathShadow.lineTo(w, h)
        pathShadow.lineTo(0f, h)
        pathShadow.close()

        // 3. Draw soft paper drop shadow
        drawPath(
            path = pathShadow,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x88000000), Color(0x22000000)),
                startY = endY,
                endY = startY + 50f
            ),
            style = Fill
        )

        // 4. Draw torn paper sheet
        drawPath(
            path = pathPaper,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF8FA5B6),
                    paperColor,
                    Color(0xFFB8C9D6)
                ),
                start = Offset(0f, startY),
                end = Offset(w, endY)
            ),
            style = Fill
        )

        // 5. Draw realistic white deckled fiber edge highlight along the rip
        pathEdgeHighlight.moveTo(edgePoints.first().x, edgePoints.first().y)
        for (i in 1 until edgePoints.size) {
            pathEdgeHighlight.lineTo(edgePoints[i].x, edgePoints[i].y)
        }

        // Thick white deckled fiber base
        drawPath(
            path = pathEdgeHighlight,
            color = Color(0xEEFFFFFF),
            style = Stroke(width = 2.4f)
        )

        // Subtle soft outer glow along the rip
        drawPath(
            path = pathEdgeHighlight,
            color = Color(0x44FFFFFF),
            style = Stroke(width = 5.0f)
        )
    }
}
