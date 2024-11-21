package com.codedbykay.purenotes.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.AddGroupButton
import com.codedbykay.purenotes.components.DrawerContent
import com.codedbykay.purenotes.components.GroupFilterMenu
import com.codedbykay.purenotes.components.GroupSortMenu
import com.codedbykay.purenotes.components.RoundedCircularProgressIndicator
import com.codedbykay.purenotes.components.SearchMenu
import com.codedbykay.purenotes.components.ToDoGroupList
import com.codedbykay.purenotes.ui.theme.ToDoAppTheme
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoGroupPage(
    toDoGroupViewModel: ToDoGroupViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToToDoPage: (Int, String) -> Unit
) {
    ToDoAppTheme(settingsViewModel) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var inputFieldVisible by remember { mutableStateOf(false) }
        var isAddMode by remember { mutableStateOf(false) }
        var isSearchMode by remember { mutableStateOf(false) }
        var inputText by remember { mutableStateOf("") }
        var isInitialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(400)
            isInitialized = true
        }

        ModalNavigationDrawer(
            gesturesEnabled = false,
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    onNavigateToSettings = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                fontSize = 26.sp,
                                text = stringResource(id = R.string.app_name)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu),
                                    contentDescription = stringResource(id = R.string.menu)
                                )
                            }
                        },
                        actions = {
                            GroupSortMenu(toDoGroupViewModel = toDoGroupViewModel)
                            GroupFilterMenu(toDoGroupViewModel = toDoGroupViewModel)
                            SearchMenu(
                                isSearchMode = true,
                                onClick = {

                                    if (!inputFieldVisible && !isSearchMode && !isAddMode) {
                                        // Case 1: Show Search Mode
                                        inputText = ""
                                        toDoGroupViewModel.setSearchQuery(null)
                                        isSearchMode = true
                                        inputFieldVisible = true
                                    } else if (inputFieldVisible && isSearchMode) {
                                        // Case 2: Close Search Mode
                                        inputFieldVisible = false
                                        isSearchMode = false
                                        inputText = ""
                                        toDoGroupViewModel.setSearchQuery(null)
                                    } else if (inputFieldVisible && !isSearchMode && isAddMode) {
                                        // Case 3: Switch from Add Mode to Search Mode
                                        isAddMode = false
                                        isSearchMode = true
                                        inputText = ""
                                        toDoGroupViewModel.setSearchQuery(null)
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
                                        toDoGroupViewModel.setSearchQuery(null)
                                        isAddMode = false
                                    } else if (inputFieldVisible && !isAddMode && isSearchMode) {
                                        // Case 3: Switch from Search Mode to Add Mode
                                        isSearchMode = false
                                        isAddMode = true
                                        inputText = ""
                                        toDoGroupViewModel.setSearchQuery(null)
                                    }
                                },
                                isActive = !isAddMode
                            )
                        }
                    )
                }
            ) { paddingValues ->
                if (isInitialized) {
                    Box(modifier = Modifier.padding(paddingValues)) {
                        ToDoGroupList(
                            toDoGroupViewModel = toDoGroupViewModel,
                            onGroupClick = { groupId, groupName ->
                                onNavigateToToDoPage(groupId, groupName)
                            }
                        )
                    }

                    // Sliding card popup
                    AnimatedVisibility(
                        visible = inputFieldVisible,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    //.fillMaxHeight(0.2f)
                                    .padding(horizontal = 0.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = inputText,
                                            onValueChange = {
                                                inputText = it
                                                if (!isAddMode) {
                                                    toDoGroupViewModel.setSearchQuery(inputText.trim())
                                                }
                                            },
                                            label = {
                                                Text(
                                                    if (isAddMode) stringResource(id = R.string.add_group_placeholder)
                                                    else stringResource(id = R.string.search_placeholder)
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp),
                                            shape = RoundedCornerShape(15.dp),
                                            colors = OutlinedTextFieldDefaults.colors(),
                                            textStyle = MaterialTheme.typography.bodyLarge
                                        )
                                        if (isAddMode) {
                                            Button(
                                                modifier = Modifier
                                                    .padding(top = 10.dp),
                                                onClick = {
                                                    toDoGroupViewModel.addGroup(
                                                        name = inputText.trim(),
                                                        createdAt = Date()
                                                    )
                                                    inputText = ""
                                                    // This hides the input field after the user has added a group
                                                    //inputFieldVisible = false
                                                    //isAddMode = false
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
    }
}
