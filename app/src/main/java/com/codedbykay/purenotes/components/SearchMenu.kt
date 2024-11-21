package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SearchMenu(
    isSearchMode: Boolean,
    onClick: () -> Unit,
    isActive: Boolean = true
) {
    IconButton(
        modifier = Modifier,
        onClick = onClick
    ) {
        if (!isActive) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.secondary
            )
        } else {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = if (isSearchMode) Icons.Default.Search else Icons.Default.Add,
                contentDescription = if (isSearchMode) "Search Mode" else "Add Mode",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

    }
}