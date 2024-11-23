package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R

@Composable
fun AddGroupButton(
    onClick: () -> Unit,
    isActive: Boolean = true
) {
    IconButton(
        modifier = Modifier.padding(end = 5.dp),
        onClick = onClick
    ) {
        if (!isActive) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.secondary
            )
        } else {
            Icon(
                modifier = Modifier
                    .size(35.dp),
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_group_button_description),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
