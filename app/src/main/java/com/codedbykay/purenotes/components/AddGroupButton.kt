package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import java.util.Date

@Composable
fun AddGroupButton(toDoGroupViewModel: ToDoGroupViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf(TextFieldValue("")) }

    IconButton(
        modifier = Modifier.padding(end = 5.dp),
        onClick = { expanded = true }
    ) {
        Icon(
            modifier = Modifier
                .size(35.dp),
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.add_group_button_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Add the new group if the name is not empty
                        if (groupName.text.isNotEmpty()) {
                            toDoGroupViewModel.addGroup(
                                name = groupName.text,
                                createdAt = Date()
                            )
                            groupName = TextFieldValue("") // Reset the input
                            expanded = false
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.add_group_button_title))
                }
            },
            dismissButton = {
                TextButton(onClick = { expanded = false }) {
                    Text(stringResource(id = R.string.cancel_add_group_button_title))
                }
            },
            title = { Text(stringResource(id = R.string.add_group_header_title)) },
            text = {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text(stringResource(id = R.string.add_group_input_placeholder)) }
                )
            }
        )
    }
}
