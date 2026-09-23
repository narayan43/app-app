package com.example.fluid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FluidState {
    var isActive by mutableStateOf(false)
    var touchX by mutableFloatStateOf(0f)
    var touchY by mutableFloatStateOf(0f)
    var smoothedY by mutableFloatStateOf(0f)
    var velocityY by mutableFloatStateOf(0f)
    var smoothedVelocityY by mutableFloatStateOf(0f)
    var deformation by mutableFloatStateOf(0f)
    var expansionProgress by mutableFloatStateOf(0f)
    var selectedIndex by mutableFloatStateOf(0f)
    var activeItem by mutableStateOf("★")
    var wobblePhase by mutableFloatStateOf(0f)
    var previousMoveDy by mutableFloatStateOf(0f)
    var springBackDisplacement by mutableFloatStateOf(0f)

    companion object {
        val ITEMS = listOf("★") + ('A'..'Z').map { it.toString() } + listOf("•")
        val ALPHABET = listOf(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '#'
        )
    }
}
