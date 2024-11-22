package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel

@Composable
fun ToDoGroupList(
    modifier: Modifier = Modifier,
    toDoGroupViewModel: ToDoGroupViewModel,
    onGroupClick: (Int, String) -> Unit
) {
    val sortedGroups by toDoGroupViewModel
        .toDoGroupList
        .observeAsState(emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {
        itemsIndexed(sortedGroups) { index, group ->
            ToDoGroupItem(
                group = group,
                onClick = { onGroupClick(group.id, group.name) },
                onUpdate = { toDoGroupViewModel.updateGroup(it.id, it.name) },
                onDelete = { toDoGroupViewModel.deleteGroupById(group.id) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            )
        }
    }
}
