package com.codedbykay.purenotes.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun DisplayDate(
    modifier: Modifier = Modifier,
    formattedDate: String,
    fontSize: TextUnit = 12.sp,
    isChecked: Boolean
) {
    Text(
        modifier = modifier,
        text = formattedDate,
        fontSize = fontSize,
        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}