package com.codedbykay.purenotes.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.customCircleBackground(color: Color): Modifier {
    return this.background(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape
    )
}