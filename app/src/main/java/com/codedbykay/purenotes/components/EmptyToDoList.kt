package com.codedbykay.purenotes.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R

@Composable
fun EmptyToDoList(
    modifier: Modifier = Modifier,
    message: String
) {
    val greatVibesFontFamily = FontFamily(
        Font(R.font.great_vibes_regular, FontWeight.Normal, FontStyle.Normal)
    )

    // Animated gradient using rememberInfiniteTransition
    val transition = rememberInfiniteTransition(label = "Gradient Animation")
    val gradientOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Gradient Offset"
    ).value

    val animatedGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        startY = gradientOffset,
        endY = gradientOffset + 1000f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedGradient)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Gradient text
        Text(
            text = message,
            style = TextStyle(
                fontFamily = greatVibesFontFamily,
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            textAlign = TextAlign.Center,
        )
    }
}
