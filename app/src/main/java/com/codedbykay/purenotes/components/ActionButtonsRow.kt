// ActionButtonsRow.kt

package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.utils.customCircleBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActionButtonsRow(
    isEditing: Boolean,
    isChecked: Boolean,
    notificationTime: Long?,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShowTimePickerClick: () -> Unit,
    onClearNotificationClick: () -> Unit,
    rowModifier: Modifier = Modifier
) {
    // State to manage the visibility of the delete confirmation dialog
    val showDeleteDialog = remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .size(22.dp)
                    .alpha(if (isChecked) 0.5f else 1f)
            )
        }

        // Date/Time IconButton (in the middle)
        IconButton(
            onClick = {
                if (notificationTime != null) {
                    onClearNotificationClick()
                } else {
                    onShowTimePickerClick()
                }
            },
            modifier = Modifier
                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                .padding(3.dp)
                .alpha(if (isChecked) 0.5f else 1f),
            enabled = !isChecked
        ) {
            Icon(
                imageVector = if (notificationTime != null) {
                    Icons.Default.Close
                } else {
                    Icons.Default.NotificationsNone
                },
                contentDescription = if (notificationTime != null) {
                    "Notification set for " + SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.ENGLISH
                    ).format(Date(notificationTime))
                } else "Set Notification",
                modifier = Modifier.size(22.dp)
            )
        }

        // Edit/Save button
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                .padding(2.dp)
                .alpha(if (isChecked) 0.5f else 1f),
            enabled = !isChecked
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
