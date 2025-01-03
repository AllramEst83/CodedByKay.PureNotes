package com.codedbykay.purenotes.components

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import kotlinx.coroutines.launch

@Composable
fun ToDoGroupList(
    modifier: Modifier = Modifier,
    toDoGroupViewModel: ToDoGroupViewModel,
    imageGalleryViewModel: ImageGalleryViewModel,
    onGroupClick: (Int, String) -> Unit,
) {
    val sortedGroups by toDoGroupViewModel
        .toDoGroupList
        .observeAsState(emptyList())

    val context = LocalContext.current
    val shareContent by toDoGroupViewModel.shareContent.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    shareContent?.let { content ->
        LaunchedEffect(content) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
            }
            context.startActivity(Intent.createChooser(intent, "Share notes"))
            toDoGroupViewModel.resetShareContent()
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(1.dp)
    ) {
        itemsIndexed(sortedGroups) { index, group ->
            ToDoGroupItem(
                group = group,
                onClick = { onGroupClick(group.id, group.name) },
                onUpdate = { toDoGroupViewModel.updateGroup(it.id, it.name) },
                onDelete = {
                    coroutineScope.launch {
                        imageGalleryViewModel.removeAllImagesFromGroup(group.id)
                        toDoGroupViewModel.deleteGroupById(group.id)
                    }
                },
                onShare = { toDoGroupViewModel.shareGroupAndToDos(group.id) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            )
        }
    }
}
