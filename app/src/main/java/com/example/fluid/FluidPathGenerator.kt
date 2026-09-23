package com.example.fluid

import androidx.compose.ui.graphics.Path
import kotlin.math.sin

object FluidPathGenerator {

    /**
     * Generates the organic liquid boundary path.
     * When swiping, a teardrop/liquid sheet pulls outward from the rail edge into the screen.
     */
    fun createFluidPath(
        isRightSide: Boolean,
        railEdgeX: Float,
        apexY: Float,
        velocityY: Float,
        expansion: Float,
        intensity: Float,
        wobble: Float,
        screenHeight: Float
    ): Path {
        val path = Path()
        if (expansion <= 0.01f) {
            return path
        }

        // Protrusion depth into screen
        val baseBulge = 90f * intensity * expansion
        val apexX = if (isRightSide) {
            railEdgeX - baseBulge
        } else {
            railEdgeX + baseBulge
        }

        // Fluid vertical spread: stretches wider when velocity is high
        val velClamped = velocityY.coerceIn(-1200f, 1200f)
        val velocityStretch = kotlin.math.abs(velClamped) * 0.04f
        val halfSpread = (140f + velocityStretch) * expansion

        // Lag in apex Y due to viscous drag
        val lagY = velClamped * 0.04f * intensity
        val actualApexY = (apexY - lagY + sin(wobble) * 4f).coerceIn(40f, screenHeight - 40f)

        val startY = (actualApexY - halfSpread).coerceAtLeast(0f)
        val endY = (actualApexY + halfSpread).coerceAtMost(screenHeight)

        // Control point calculations for natural organic water curve
        val directionSign = if (isRightSide) -1f else 1f

        val topControlX1 = railEdgeX
        val topControlY1 = startY + halfSpread * 0.35f

        val topControlX2 = railEdgeX + directionSign * (baseBulge * 0.85f)
        val topControlY2 = actualApexY - halfSpread * 0.30f - (lagY * 0.3f)

        val bottomControlX1 = railEdgeX + directionSign * (baseBulge * 0.85f)
        val bottomControlY1 = actualApexY + halfSpread * 0.30f - (lagY * 0.3f)

        val bottomControlX2 = railEdgeX
        val bottomControlY2 = endY - halfSpread * 0.35f

        path.reset()
        // Begin from the rail edge at startY
        path.moveTo(railEdgeX, startY)

        // Curve from edge towards the liquid apex
        path.cubicTo(
            x1 = topControlX1,
            y1 = topControlY1,
            x2 = topControlX2,
            y2 = topControlY2,
            x3 = apexX,
            y3 = actualApexY
        )

        // Curve from the apex back down to the rail edge
        path.cubicTo(
            x1 = bottomControlX1,
            y1 = bottomControlY1,
            x2 = bottomControlX2,
            y2 = bottomControlY2,
            x3 = railEdgeX,
            y3 = endY
        )

        // Close along the rail edge
        path.lineTo(railEdgeX, startY)
        path.close()

        return path
    }

    /**
     * Generates an inner specular highlight curve to give the fluid a glassy, tactile depth.
     */
    fun createHighlightPath(
        isRightSide: Boolean,
        railEdgeX: Float,
        apexY: Float,
        velocityY: Float,
        expansion: Float,
        intensity: Float,
        screenHeight: Float
    ): Path {
        val path = Path()
        if (expansion <= 0.05f) return path

        val baseBulge = 84f * intensity * expansion
        val apexX = if (isRightSide) railEdgeX - baseBulge else railEdgeX + baseBulge
        val lagY = velocityY.coerceIn(-1000f, 1000f) * 0.035f
        val actualApexY = (apexY - lagY).coerceIn(50f, screenHeight - 50f)
        val halfSpread = 100f * expansion

        val startY = actualApexY - halfSpread
        val endY = actualApexY + halfSpread
        val directionSign = if (isRightSide) -1f else 1f

        path.reset()
        path.moveTo(railEdgeX + directionSign * 6f, startY)
        path.cubicTo(
            x1 = railEdgeX + directionSign * (baseBulge * 0.3f),
            y1 = startY + halfSpread * 0.4f,
            x2 = railEdgeX + directionSign * (baseBulge * 0.95f),
            y2 = actualApexY - halfSpread * 0.25f,
            x3 = apexX + directionSign * 4f,
            y3 = actualApexY
        )
        path.cubicTo(
            x1 = railEdgeX + directionSign * (baseBulge * 0.95f),
            y1 = actualApexY + halfSpread * 0.25f,
            x2 = railEdgeX + directionSign * (baseBulge * 0.3f),
            y2 = endY - halfSpread * 0.4f,
            x3 = railEdgeX + directionSign * 6f,
            y3 = endY
        )

        return path
    }
}
