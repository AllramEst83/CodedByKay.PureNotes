package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.utils.blendWith
import com.codedbykay.purenotes.viewModels.ToDoViewModel

@Composable
fun ToDoListContainer(
    modifier: Modifier = Modifier,
    toDoViewModel: ToDoViewModel,
    groupId: Int,
    toDoList: List<ToDo>
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    if (toDoList.isNotEmpty()) {
        // Separate the to-do items into completed and pending lists
        val pendingItems = toDoList.filter { !it.done }
        val completedItems = toDoList.filter { it.done }

        LazyColumn(
            modifier = modifier
        ) {
            // Pending Items Section
            if (pendingItems.isNotEmpty()) {
                itemsIndexed(pendingItems) { _, item ->
                    ToDoListItem(
                        toDoItem = item,
                        toDoViewModel = toDoViewModel,
                        onDelete = {
                            toDoViewModel
                                .deleteToDo(
                                    item.id,
                                    item.notificationRequestCode,
                                    item.notificationAction,
                                    item.notificationDataUri
                                )
                        },
                        onUpdate = { updatedItem -> toDoViewModel.updateToDoAfterEdit(updatedItem) },
                        onDoneUpdate = { updatedDone ->
                            toDoViewModel.updateToDoDone(
                                item.id,
                                updatedDone
                            )
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }

            // Completed Items Section
            if (completedItems.isNotEmpty()) {
                item {
                    // Pill-shaped container for "Completed" title and delete icon
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 15.dp, bottom = 10.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                            .copy(alpha = 0.8f)
                            .blendWith(MaterialTheme.colorScheme.primaryContainer, 0.2f)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 2.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(
                                onClick = {
                                    showDeleteDialog.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete all completed",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                itemsIndexed(completedItems) { _, item ->
                    ToDoListItem(
                        toDoItem = item,
                        toDoViewModel = toDoViewModel,
                        onDelete = {
                            toDoViewModel.deleteToDo(
                                item.id,
                                item.notificationRequestCode,
                                item.notificationAction,
                                item.notificationDataUri
                            )
                        },
                        onUpdate = { updatedItem -> toDoViewModel.updateToDoAfterEdit(updatedItem) },
                        onDoneUpdate = { updatedDone ->
                            toDoViewModel.updateToDoDone(item.id, updatedDone)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }
    } else {
        EmptyToDoList(
            modifier = modifier,
            message = stringResource(id = R.string.empty_list_message)
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value) {

        DeleteAlertModal(
            onDelete = {
                toDoViewModel.deleteAllDoneToDos(groupId)
                showDeleteDialog.value = false
            },
            showDeleteDialog,
            confirmationText = stringResource(id = R.string.alert_delete_done_notes_text)
        )
    }
}
