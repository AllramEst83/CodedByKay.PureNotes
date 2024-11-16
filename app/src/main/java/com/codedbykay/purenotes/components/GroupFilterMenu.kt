package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.codedbykay.purenotes.models.ToDoGroupFilter
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel

@Composable
fun GroupFilterMenu(toDoGroupViewModel: ToDoGroupViewModel) {
    var expanded by remember { mutableStateOf(false) }

    // Observe the current filter state
    val currentFilter by toDoGroupViewModel.groupFilter.observeAsState(ToDoGroupFilter())

    IconButton(
        modifier = Modifier.padding(vertical = 2.dp),
        onClick = { expanded = true }) {
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
        // Section for Created Date Filter
        Text(
            text = stringResource(id = R.string.filter_created_header),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.filter_all)) },
            onClick = {
                toDoGroupViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.ALL))
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
                toDoGroupViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.TODAY))
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
                toDoGroupViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_WEEK))
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
                toDoGroupViewModel.setFilter(currentFilter.copy(createdDateFilter = CreatedDateFilter.THIS_MONTH))
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
