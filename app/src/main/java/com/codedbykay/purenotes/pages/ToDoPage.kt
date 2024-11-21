package com.codedbykay.purenotes.pages

import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavHostController
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.FilterMenu
import com.codedbykay.purenotes.components.RoundedCircularProgressIndicator
import com.codedbykay.purenotes.components.SearchMenu
import com.codedbykay.purenotes.components.SortMenu
import com.codedbykay.purenotes.components.ToDoListContainer
import com.codedbykay.purenotes.utils.handleEmptyBackNavigation
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoPage(
    toDoViewModel: ToDoViewModel,
    groupId: Int,
    groupName: String,
    navController: NavHostController
) {
    val toDoList by toDoViewModel.toDoList.observeAsState(emptyList())
    var inputText by remember { mutableStateOf("") }
    var isSearchMode by remember { mutableStateOf(false) }
    var backButtonEnabled by remember { mutableStateOf(true) }
    var isInitialized by remember { mutableStateOf(false) }

    // BackHandler to manage system back presses
    BackHandler(enabled = backButtonEnabled) {
        backButtonEnabled = false
        handleEmptyBackNavigation(navController)
    }

    LaunchedEffect(groupId) {
        delay(200)
        if (!backButtonEnabled) {
            delay(500)
            backButtonEnabled = true
        }

        toDoViewModel.setGroupId(groupId)
        isInitialized = true
    }

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
                    IconButton(
                        onClick = {
                            handleEmptyBackNavigation(navController)
                        },
                        enabled = backButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    SortMenu(toDoViewModel = toDoViewModel)
                    FilterMenu(toDoViewModel = toDoViewModel)
                    SearchMenu(
                        isSearchMode = !isSearchMode,
                        onClick = {
                            isSearchMode = !isSearchMode
                            inputText = ""
                            if (!isSearchMode) {
                                toDoViewModel.setSearchQuery(null)
                            }
                        },
                        isActive = true
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isInitialized) {
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
                        toDoList = toDoList,
                        isSearchMode
                    )
                }

                // Bottom Input Field and Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        value = inputText,
                        onValueChange = { newText ->
                            inputText = newText

                            if (isSearchMode) {
                                toDoViewModel.setSearchQuery(newText.trim())
                            }
                        },
                        label = {
                            Text(
                                if (isSearchMode)
                                    stringResource(id = R.string.search_note_input)
                                else
                                    stringResource(id = R.string.add_note_input)
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                    )
                    if (!isSearchMode) {
                        Button(
                            onClick = {
                                toDoViewModel.addToDo(inputText.trim(), groupId = groupId)
                                inputText = ""
                                toDoViewModel.setSearchQuery("")
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
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                RoundedCircularProgressIndicator()
            }
        }
    }
}
