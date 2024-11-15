package com.codedbykay.purenotes.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

// Extension function to blend a color with another color
fun Color.blendWith(otherColor: Color, blendFactor: Float): Color {
    return lerp(this, otherColor, blendFactor)
}

fun Color.contrastColor(): Color {
    return if (this.luminance() > 0.5f) Color.Black else Color.White
}

fun Color.adjustedForStatusBar(darkenFactor: Float = 0.3f): Color {
    return if (this.luminance() > 0.5f) this.darken(darkenFactor) else this
}

fun Color.darken(factor: Float): Color {
    // Apply the darkening uniformly across RGB
    val darkenedRed = (red * (1 - factor)).coerceIn(0f, 1f)
    val darkenedGreen = (green * (1 - factor)).coerceIn(0f, 1f)
    val darkenedBlue = (blue * (1 - factor)).coerceIn(0f, 1f)
    return Color(darkenedRed, darkenedGreen, darkenedBlue, alpha)
}

fun Color.blendWithWhite(ratio: Float = 0.5f, brightnessReduction: Float = 0.1f): Color {
    // Ratio determines how much white is mixed in; brightnessReduction slightly darkens the color
    return Color(
        red = (red + (1 - red) * ratio) * (1 - brightnessReduction),
        green = (green + (1 - green) * ratio) * (1 - brightnessReduction),
        blue = (blue + (1 - blue) * ratio) * (1 - brightnessReduction)
    )
}


