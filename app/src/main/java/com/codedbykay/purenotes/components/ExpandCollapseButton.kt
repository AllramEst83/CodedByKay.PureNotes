package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ExpandCollapseButton(
    expanded: Boolean,
    onClick: () -> Unit,
    rotation: Float,
    iconModifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = iconModifier
    ) {
        Box(
            modifier = boxModifier
        ) {
            Icon(
                imageVector = Icons.Filled.ExpandLess,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(22.dp)
                    .rotate(rotation)
            )
        }
    }
}
