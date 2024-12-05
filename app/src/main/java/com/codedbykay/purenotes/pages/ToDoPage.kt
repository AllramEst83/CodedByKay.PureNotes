package com.codedbykay.purenotes.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.AddGroupButton
import com.codedbykay.purenotes.components.FilterMenu
import com.codedbykay.purenotes.components.RoundedCircularProgressIndicator
import com.codedbykay.purenotes.components.SearchMenu
import com.codedbykay.purenotes.components.SortMenu
import com.codedbykay.purenotes.components.ToDoListContainer
import com.codedbykay.purenotes.utils.handleEmptyBackNavigation
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoPage(
    toDoViewModel: ToDoViewModel,
    imageGalleryViewModel: ImageGalleryViewModel,
    groupId: Int,
    groupName: String,
    navController: NavHostController,
) {
    val toDoList by toDoViewModel.toDoList.observeAsState(emptyList())
    var inputText by remember { mutableStateOf("") }
    var inputFieldVisible by remember { mutableStateOf(false) }
    var isAddMode by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    var backButtonEnabled by remember { mutableStateOf(true) }
    var isInitialized by remember { mutableStateOf(false) }
    var inputFieldHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val transition =
        updateTransition(
            targetState = inputFieldVisible,
            label = "InputFieldTransition"
        )

    val inputFieldOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "InputFieldOffset"
    ) { visible ->
        if (visible) 0.dp else -inputFieldHeight
    }

    val listPaddingTop by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "ListPaddingTop"
    ) { visible ->
        if (visible) inputFieldHeight else 0.dp
    }

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
                        isSearchMode = true,
                        onClick = {

                            if (!inputFieldVisible && !isSearchMode && !isAddMode) {
                                // Case 1: Show Search Mode
                                inputText = ""
                                toDoViewModel.setSearchQuery(null)
                                isSearchMode = true
                                inputFieldVisible = true
                            } else if (inputFieldVisible && isSearchMode) {
                                // Case 2: Close Search Mode
                                inputFieldVisible = false
                                isSearchMode = false
                                inputText = ""
                                toDoViewModel.setSearchQuery(null)
                            } else if (inputFieldVisible && !isSearchMode && isAddMode) {
                                // Case 3: Switch from Add Mode to Search Mode
                                isAddMode = false
                                isSearchMode = true
                                inputText = ""
                                toDoViewModel.setSearchQuery(null)
                            }
                        },
                        isActive = !isSearchMode
                    )
                    AddGroupButton(
                        onClick = {
                            if (!inputFieldVisible && !isAddMode && !isSearchMode) {
                                // Case 1: Show Add Mode
                                isAddMode = true
                                isSearchMode = false
                                inputText = ""
                                inputFieldVisible = true
                            } else if (inputFieldVisible && isAddMode) {
                                // Case 2: Close Add Mode
                                inputFieldVisible = false
                                inputText = ""
                                toDoViewModel.setSearchQuery(null)
                                isAddMode = false
                            } else if (inputFieldVisible && !isAddMode && isSearchMode) {
                                // Case 3: Switch from Search Mode to Add Mode
                                isSearchMode = false
                                isAddMode = true
                                inputText = ""
                                toDoViewModel.setSearchQuery(null)
                            }
                        },
                        isActive = !isAddMode
                    )
                }
            )
        }
    ) { paddingValues ->

        if (isInitialized) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                // Input field animated in from the top
                if (inputFieldVisible || transition.currentState) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = inputFieldOffset)
                            .zIndex(1f)
                            .wrapContentHeight()
                            .onGloballyPositioned { coordinates ->
                                // Convert height from pixels to Dp
                                val heightPx = coordinates.size.height.toFloat()
                                val heightDp = with(density) { heightPx.toDp() }
                                inputFieldHeight = heightDp
                            },

                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = {
                                        inputText = it
                                        if (!isAddMode) {
                                            toDoViewModel.setSearchQuery(inputText.trim())
                                        }
                                    },
                                    label = {
                                        Text(
                                            if (isAddMode) stringResource(id = R.string.add_note_input)
                                            else stringResource(id = R.string.search_placeholder)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    shape = RoundedCornerShape(15.dp),
                                    textStyle = TextStyle(
                                        fontSize = 22.sp
                                    )
                                )
                                if (isAddMode) {
                                    Button(
                                        modifier = Modifier
                                            .padding(top = 10.dp),
                                        onClick = {
                                            toDoViewModel.addToDo(
                                                inputText.trim(),
                                                groupId = groupId
                                            )
                                            inputText = ""
                                            // Optionally hide the input field after adding
                                            // inputFieldVisible = false
                                            // isAddMode = false
                                        },
                                        enabled = inputText.trim().isNotEmpty()
                                    ) {
                                        Text(stringResource(id = R.string.add_button))
                                    }
                                }
                            }
                        }
                    }
                }
                // Display the list of ToDos
                ToDoListContainer(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                        .padding(top = listPaddingTop),
                    toDoViewModel = toDoViewModel,
                    imageGalleryViewModel = imageGalleryViewModel,
                    groupId = groupId,
                    toDoList = toDoList
                )
            }

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                //Spinner
                RoundedCircularProgressIndicator()
            }
        }
    }
}
