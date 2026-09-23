package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.fluid.FluidPathGenerator
import com.example.fluid.FluidState
import com.example.model.AlphabetPosition
import com.example.model.FluidIntensity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Renders the compact, shorter-in-height Alphabet Slide Bar inspired by the user's screenshots.
 * - Shorter in height (approx 52% of screen height, vertically centered).
 * - Starts with '★' for top/favorites, followed by 'A'..'Z' and '•'.
 * - Fluid arc deformation that follows the thumb.
 * - Circular dark magnifying bubble lens [ V ] that appears right at the thumb apex.
 */
@Composable
fun FluidAlphabetOverlay(
    fluidState: FluidState,
    alphabetPosition: AlphabetPosition,
    fluidIntensity: FluidIntensity,
    fluidColor: Color,
    modifier: Modifier = Modifier
) {
    val isRightSide = alphabetPosition == AlphabetPosition.RIGHT

    // Spring animatable for smooth fluid expansion and retraction
    val expansionAnim = remember { Animatable(0f) }

    LaunchedEffect(fluidState.isActive) {
        if (fluidState.isActive) {
            expansionAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            expansionAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    fluidState.expansionProgress = expansionAnim.value

    val intensityMultiplier = when (fluidIntensity) {
        FluidIntensity.LOW -> 0.75f
        FluidIntensity.MEDIUM -> 1.0f
        FluidIntensity.HIGH -> 1.35f
    }

    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
    }

    val bubbleFillPaint = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.BLACK
        }
    }

    val bubbleStrokePaint = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = android.graphics.Color.parseColor("#FFFFFF")
        }
    }

    val bubbleGlowPaint = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.BLACK
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val expansion = expansionAnim.value

        // Shorter in height slide bar:
        // Positioned on the right side, occupying ~52% of the screen height, vertically centered
        val railHeight = h * 0.54f
        val railTop = (h - railHeight) / 2f + (h * 0.05f) // Slight offset below clock
        val railBottom = railTop + railHeight

        val railEdgeX = if (isRightSide) w else 0f
        val railBaseX = if (isRightSide) w - 18.dp.toPx() else 18.dp.toPx()

        // Alphabet items: Star + A..Z + Dot
        val alphabetItems = listOf("★") + ('A'..'Z').map { it.toString() } + listOf("•")
        val totalItems = alphabetItems.size
        val stepY = railHeight / (totalItems - 1)

        val selectedContinuousIdx = fluidState.selectedIndex
        val directionSign = if (isRightSide) -1f else 1f

        drawIntoCanvas { composeCanvas ->
            val nativeCanvas = composeCanvas.nativeCanvas

            // 1. Draw individual alphabet characters with fluid displacement & curvature
            for (i in 0 until totalItems) {
                val itemStr = alphabetItems[i]
                val defaultY = railTop + i * stepY

                // Index distance in continuous space
                val indexDist = abs(selectedContinuousIdx - i)

                // Smooth bell-shaped curve for letters near the thumb
                val bulgeFactor = if (expansion > 0.01f && indexDist < 5.0f) {
                    val proximity = (1f - (indexDist / 5.0f)).coerceIn(0f, 1f)
                    proximity * proximity * (3f - 2f * proximity)
                } else {
                    0f
                }

                // Horizontal displacement pulling toward screen center (like screenshot 2 & 3)
                val maxDisplacement = 46.dp.toPx() * intensityMultiplier * expansion
                val letterX = railBaseX + directionSign * (bulgeFactor * maxDisplacement)
                val letterY = defaultY

                val isSelected = indexDist < 0.6f && fluidState.isActive
                val scale = if (expansion > 0.01f) {
                    1f + bulgeFactor * 0.45f
                } else {
                    1f
                }

                val alpha = if (fluidState.isActive) {
                    when {
                        indexDist < 0.6f -> 1.0f
                        indexDist < 2.5f -> 0.85f
                        indexDist < 5.0f -> 0.55f
                        else -> 0.22f
                    }
                } else {
                    0.45f
                }

                // If this is the active selected letter, draw the circular Magnifying Bubble Lens!
                if (isSelected && expansion > 0.35f) {
                    val bubbleRadius = 34.dp.toPx()
                    val bubbleCenterX = letterX + directionSign * 56.dp.toPx()
                    val bubbleCenterY = letterY

                    // Soft shadow
                    nativeCanvas.drawCircle(bubbleCenterX, bubbleCenterY + 4f, bubbleRadius + 3f, bubbleGlowPaint)

                    // Dark circle fill (pure dark background for contrast)
                    nativeCanvas.drawCircle(bubbleCenterX, bubbleCenterY, bubbleRadius, bubbleFillPaint)

                    // White outline border
                    nativeCanvas.drawCircle(bubbleCenterX, bubbleCenterY, bubbleRadius, bubbleStrokePaint)

                    // Big, clear white active letter inside bubble
                    textPaint.textSize = 28.dp.toPx()
                    textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    textPaint.color = android.graphics.Color.WHITE
                    val metrics = textPaint.fontMetrics
                    val bubbleBaseY = bubbleCenterY - (metrics.ascent + metrics.descent) / 2f
                    nativeCanvas.drawText(itemStr, bubbleCenterX, bubbleBaseY, textPaint)
                }

                // Draw the rail character
                val baseFontSize = 10.5.dp.toPx()
                textPaint.textSize = baseFontSize * scale
                textPaint.typeface = if (isSelected) {
                    Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                } else {
                    Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                }

                val textColor = if (isSelected) {
                    Color.White
                } else if (bulgeFactor > 0.1f) {
                    Color(0xFFEEEEEE)
                } else {
                    Color(0xFF88888E)
                }

                textPaint.color = textColor.copy(alpha = alpha).toArgb()

                val fontMetrics = textPaint.fontMetrics
                val baselineY = letterY - (fontMetrics.ascent + fontMetrics.descent) / 2f

                nativeCanvas.drawText(itemStr, letterX, baselineY, textPaint)
            }
        }
    }
}
