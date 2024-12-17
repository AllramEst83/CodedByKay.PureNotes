package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.DoneStatus
import com.codedbykay.purenotes.models.NotificationTimeFilter
import com.codedbykay.purenotes.models.ToDoFilter
import com.codedbykay.purenotes.viewModels.ToDoViewModel

@Composable
fun FilterMenu(
    toDoViewModel: ToDoViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    // Observe the current filter state
    val currentFilter by toDoViewModel.filter.observeAsState(ToDoFilter())

    IconButton(onClick = { expanded = true }) {
        Icon(
            modifier = Modifier.size(35.dp),
            imageVector = Icons.Default.FilterList,
            contentDescription = stringResource(id = R.string.filter),
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        // Section for Done Status Filter
        Text(
            text = stringResource(id = R.string.filter_done_status_header),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_status_all_done),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatus.ALL))
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatus.ALL) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_status_done),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatus.DONE))
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatus.DONE) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_status_not_done),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatus.NOT_DONE))
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatus.NOT_DONE) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Divider between filter sections
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Section for Created Date Filter
        Text(
            text = stringResource(id = R.string.filter_created_header),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_all),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.ALL))
            },
            trailingIcon = {
                if (currentFilter.createdDateFilter == CreatedDateFilter.ALL) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_today),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.TODAY))
            },
            trailingIcon = {
                if (currentFilter.createdDateFilter == CreatedDateFilter.TODAY) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_this_week),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_WEEK))
            },
            trailingIcon = {
                if (currentFilter.createdDateFilter == CreatedDateFilter.THIS_WEEK) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_this_month),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_MONTH))
            },
            trailingIcon = {
                if (currentFilter.createdDateFilter == CreatedDateFilter.THIS_MONTH) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Divider between filter sections
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Section for Notification Time Filter
        Text(
            text = stringResource(id = R.string.filter_notification_time_header),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_notification_time_unfiltered),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(notificationDateFilter = NotificationTimeFilter.ALL))
            },
            trailingIcon = {
                if (currentFilter.notificationDateFilter == NotificationTimeFilter.ALL) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_today),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(notificationDateFilter = NotificationTimeFilter.TODAY))
            },
            trailingIcon = {
                if (currentFilter.notificationDateFilter == NotificationTimeFilter.TODAY) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_this_week),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(notificationDateFilter = NotificationTimeFilter.THIS_WEEK))
            },
            trailingIcon = {
                if (currentFilter.notificationDateFilter == NotificationTimeFilter.THIS_WEEK) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.filter_this_month),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(notificationDateFilter = NotificationTimeFilter.THIS_MONTH))
            },
            trailingIcon = {
                if (currentFilter.notificationDateFilter == NotificationTimeFilter.THIS_MONTH) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

    }
}
