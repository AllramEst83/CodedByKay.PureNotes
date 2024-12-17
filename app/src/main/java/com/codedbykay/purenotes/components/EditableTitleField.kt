package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R

@Composable
fun EditableTitleField(
    editModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    createdDateModifier: Modifier = Modifier,
    title: String,
    createdDate: String,
    onTitleChange: (String) -> Unit,
    isEditing: Boolean,
    isChecked: Boolean,
) {
    if (isEditing) {
        // Edit tile with a TextField
        TextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = editModifier,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.edit_title),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                letterSpacing = 0.5.sp
            )
        )
    } else {
        // Title text
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifier,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
        // Created date text
        Text(
            modifier = createdDateModifier,
            text = createdDate,
            style = MaterialTheme.typography.labelSmall,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}