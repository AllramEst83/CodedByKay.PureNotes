package com.codedbykay.purenotes.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.FilterMenu
import com.codedbykay.purenotes.components.SortMenu
import com.codedbykay.purenotes.components.ToDoListContainer
import com.codedbykay.purenotes.viewModels.ToDoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoPage(
    toDoViewModel: ToDoViewModel,
    groupId: Int,
    groupName: String,
    onBack: () -> Unit
) {
    LaunchedEffect(groupId) {
        toDoViewModel.setGroupId(groupId)
    }

    // Observe to-do items for the specified group ID
    val toDoList by toDoViewModel.toDoList.observeAsState(emptyList())
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fontSize = 26.sp,
                        text = groupName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    SortMenu(toDoViewModel = toDoViewModel)
                    FilterMenu(toDoViewModel = toDoViewModel)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display the list of ToDos
            Box(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
            ) {
                ToDoListContainer(
                    toDoViewModel = toDoViewModel,
                    groupId,
                    toDoList = toDoList

                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text(stringResource(id = R.string.add_note_input)) },
                    shape = RoundedCornerShape(15.dp),
                )
                Button(
                    onClick = {
                        // Add to-do item with the specified group ID
                        toDoViewModel.addToDo(inputText.trim(), groupId = groupId)
                        inputText = ""
                    },
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, top = 9.dp, bottom = 2.dp),
                    enabled = inputText.trim().isNotEmpty()
                ) {
                    Text(stringResource(id = R.string.add_button))
                }
            }
        }
    }
}
