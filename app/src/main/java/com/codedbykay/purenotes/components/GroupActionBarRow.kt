package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.utils.customCircleBackground

@Composable
fun GroupActionButtonsRow(
    isEditing: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    rowModifier: Modifier = Modifier
) {
    // State to manage the visibility of the delete confirmation dialog
    val showDeleteDialog = remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Delete button
        IconButton(
            onClick = { showDeleteDialog.value = true },
            modifier = Modifier
                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(22.dp)
            )
        }

        // Share button
        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share icon",
                modifier = Modifier.size(22.dp)
            )
        }

        // Edit button
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                contentDescription = if (isEditing) "Save" else "Edit",
                modifier = Modifier.size(22.dp)
            )
        }
    }
    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}