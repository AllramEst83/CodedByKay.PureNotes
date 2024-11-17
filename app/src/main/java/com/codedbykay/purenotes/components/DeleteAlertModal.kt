package com.codedbykay.purenotes.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.codedbykay.purenotes.R

@Composable
fun DeleteAlertModal(
    onDelete: () -> Unit,
    showDeleteDialog: MutableState<Boolean>,
    confirmationText: String
) {
    AlertDialog(
        onDismissRequest = { showDeleteDialog.value = false },
        title = { Text(text = stringResource(id = R.string.alert_delete_done_notes_title)) },
        text = { Text(confirmationText) },
        confirmButton = {
            Button(
                onClick = {
                    onDelete()
                }
            ) {
                Text(stringResource(id = R.string.button_delete_text))
            }
        },
        dismissButton = {
            Button(
                onClick = { showDeleteDialog.value = false }
            ) {
                Text(stringResource(id = R.string.button_cancel_text))
            }
        }
    )
}