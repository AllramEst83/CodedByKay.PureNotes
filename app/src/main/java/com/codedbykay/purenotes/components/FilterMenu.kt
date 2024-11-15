package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Check
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
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.DoneStatusFilter
import com.codedbykay.purenotes.models.ToDoFilter
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import com.codedbykay.purenotes.R

@Composable
fun FilterMenu(toDoViewModel: ToDoViewModel) {
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
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.filter_status_all_done)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatusFilter.ALL))
                expanded = false
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatusFilter.ALL) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.filter_status_done)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatusFilter.DONE))
                expanded = false
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatusFilter.DONE) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.filter_status_not_done)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(doneStatus = DoneStatusFilter.NOT_DONE))
                expanded = false
            },
            trailingIcon = {
                if (currentFilter.doneStatus == DoneStatusFilter.NOT_DONE) {
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
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.filter_all)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.ALL))
                expanded = false
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
            text = { Text(stringResource(id = R.string.filter_today)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.TODAY))
                expanded = false
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
            text = { Text(stringResource(id = R.string.filter_this_week)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_WEEK))
                expanded = false
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
            text = { Text(stringResource(id = R.string.filter_this_month)) },
            onClick = {
                toDoViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_MONTH))
                expanded = false
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
    }
}
