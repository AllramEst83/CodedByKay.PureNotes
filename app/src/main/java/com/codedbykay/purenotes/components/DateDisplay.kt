package com.codedbykay.purenotes.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun DisplayDate(
    modifier: Modifier = Modifier,
    formattedDate: String,
    isChecked: Boolean
) {
    Text(
        modifier = modifier,
        text = formattedDate,
        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}