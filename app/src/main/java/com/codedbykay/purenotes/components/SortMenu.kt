package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
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
import com.codedbykay.purenotes.models.SortOrder
import com.codedbykay.purenotes.viewModels.ToDoViewModel

@Composable
fun SortMenu(toDoViewModel: ToDoViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val sortOrder by toDoViewModel.sortOrder.observeAsState(SortOrder.CREATED_AT_DESCENDING)

    IconButton(
        onClick = { expanded = true }
    ) {
        Icon(
            modifier = Modifier
                .size(35.dp)
                .padding(end = 5.dp),
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = stringResource(id = R.string.sort),
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_created_at_desc)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.CREATED_AT_DESCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.CREATED_AT_DESCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_created_at_asc)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.CREATED_AT_ASCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.CREATED_AT_ASCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_title_a_z)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.TITLE_ASCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.TITLE_ASCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_title_z_a)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.TITLE_DESCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.TITLE_DESCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_notification_time_desc)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.NOTIFICATION_TIME_ASCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.NOTIFICATION_TIME_ASCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_notification_time_asc)) },
            onClick = {
                toDoViewModel.setSortOrder(SortOrder.NOTIFICATION_TIME_DESCENDING)
            },
            trailingIcon = {
                if (sortOrder == SortOrder.NOTIFICATION_TIME_DESCENDING) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.filter_and_sort_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        // Add more DropdownMenuItems here if you implement additional sort options
    }
}