package com.codedbykay.purenotes.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun SearchMenu(
    isSearchMode: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isSearchMode) Icons.Default.Add else Icons.Default.Search,
            contentDescription = if (isSearchMode) "Add Mode" else "Search Mode",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}