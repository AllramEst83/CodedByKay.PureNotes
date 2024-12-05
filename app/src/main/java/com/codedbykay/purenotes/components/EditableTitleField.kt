package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
    title: String,
    onTitleChange: (String) -> Unit,
    isEditing: Boolean,
    isChecked: Boolean
) {
    if (isEditing) {
        // Edit tile with a TextField
        TextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = editModifier,
            placeholder = { Text(stringResource(id = R.string.edit_title)) },
            textStyle = TextStyle(
                fontSize = 22.sp,
                letterSpacing = 0.5.sp
            )
        )
    } else {
        // Title text
        Text(
            text = title,
            fontSize = 22.sp,
            modifier = textModifier,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}