import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )

    val transition = rememberInfiniteTransition("infiniteTransition")
    val translateAnim by transition.animateFloat(
        label = "animateFloat",
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(translateAnim, translateAnim)
    )

    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Placeholder for Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(brush, shape = MaterialTheme.shapes.small)
                .alpha(0.5f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Placeholder for title
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.7f)
                    .background(brush, shape = MaterialTheme.shapes.small)
                    .alpha(0.5f)
            )

            // Placeholder for date
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.5f)
                    .background(brush, shape = MaterialTheme.shapes.small)
                    .alpha(0.5f)
            )
        }
    }
}
