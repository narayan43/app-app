package com.example.model

import androidx.compose.ui.graphics.ImageBitmap

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: ImageBitmap? = null,
    val firstChar: Char = '#',
    val isPinned: Boolean = false,
    val installTime: Long = 0L
) {
    val uniqueKey: String
        get() = if (activityName.isNotEmpty()) "$packageName/$activityName" else packageName
}
