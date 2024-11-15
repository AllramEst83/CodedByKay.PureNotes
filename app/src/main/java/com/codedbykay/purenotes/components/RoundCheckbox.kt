package com.codedbykay.purenotes.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.utils.blendWith
import com.codedbykay.purenotes.utils.customCircleBackground

@Composable
fun RoundCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Detecting current theme for contrast adjustment
    val backgroundColor = if (isChecked) {
        if (!isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.primary.blendWith(
                Color.Green,
                0.35f
            ) // Lighter green in light mode
        } else {
            MaterialTheme.colorScheme.primary.blendWith(
                Color.DarkGray,
                0.5f
            ) // Darker greenish-gray in dark mode
        }
    } else {
        if (!isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant.blendWith(
                Color.LightGray,
                0.7f
            ) // Dimmed for unchecked in light mode
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) // Darker dimmed for unchecked in dark mode
        }
    }

    IconButton(
        onClick = { onCheckedChange(!isChecked) },
        modifier = modifier
            .padding(end = 8.dp)
            .customCircleBackground(backgroundColor)
            .size(22.dp)
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked,
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            tint = Color.White // Always use white tint for high contrast
        )
    }
}
