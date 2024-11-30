package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditableContentField(
    editModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    content: String,
    onContentChange: (String) -> Unit,
    isEditing: Boolean,
    isChecked: Boolean
) {
    if (isEditing) {
        // Editable content with a TextField
        TextField(
            value = content,
            onValueChange = onContentChange,
            modifier = editModifier,
            placeholder = { Text("Edit content") },
            textStyle = TextStyle(
                fontSize = 20.sp,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
    } else {
        Spacer(modifier = Modifier.height(4.dp))
        // Content text
        Text(
            modifier = textModifier,
            text = content,
            fontSize = 18.sp,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}