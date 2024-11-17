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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R
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
                .padding(3.dp),
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
                .padding(2.dp),
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

        DeleteAlertModal(
            onDelete = {
                onDeleteClick()
                showDeleteDialog.value = false
            },
            showDeleteDialog,
            confirmationText = stringResource(R.string.alert_delete_confirmation_text)
        )
    }
}
