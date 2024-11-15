package com.codedbykay.purenotes.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R

@Composable
fun EmptyToDoList() {
    // Infinite Transition for animating gradient position
    val transition = rememberInfiniteTransition(
        label = "Gradient Animation"
    )
    val gradientOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Gradient Offset"
    ).value

    // Animated Gradient
    val animatedGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        startY = gradientOffset, // Dynamic start position
        endY = gradientOffset + 1000f // Dynamic end position
    )

    // Animation for scaling the icon
    val scale = rememberInfiniteTransition(label = "Scale Animation")
    val animatedScale = scale.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Icon Scale"
    ).value

    // Animation for the text bounce
    val animatedOffset = scale.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Text Bounce"
    ).value

    // Main content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedGradient)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with scaling animation
            Icon(
                painter = painterResource(id = R.drawable.ic_emptylist), // Replace with your icon resource
                contentDescription = "Empty List",
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text with bounce animation
            Text(
                text = "Oops! Nothing here.",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .offset(y = animatedOffset.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false // Ensures shadow appears outside the gradient
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFF9A8B), Color(0xFFFF6A88))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ), // Padding to separate text from gradient background
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    shadow = Shadow(
                        color = Color(0x80000000), // Semi-transparent black
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}
