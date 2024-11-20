package com.codedbykay.purenotes.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 8.dp,
    size: Dp = 68.dp
) {
    // Infinite transition for the rotation animation
    val infiniteTransition = rememberInfiniteTransition(
        label = ""
    )

    // Rotation animation from 0 to 360 degrees
    val rotation by infiniteTransition.animateFloat(
        label = "",
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = TweenSpec(durationMillis = 1200, easing = LinearEasing)
        )
    )

    // Sweep angle animation from 0 to 270 degrees and back to 0
    val sweepAngle by infiniteTransition.animateFloat(
        label = "",
        initialValue = 0f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            animation = TweenSpec(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = modifier
            .size(size)
            .rotate(rotation)
    ) {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round
        )

        // Define the diameter and top-left offset of the arc
        val diameter = size.toPx() - stroke.width
        val topLeftOffset = stroke.width / 2

        // Draw the arc with rounded edges
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(topLeftOffset, topLeftOffset),
            size = Size(diameter, diameter),
            style = stroke
        )
    }
}
