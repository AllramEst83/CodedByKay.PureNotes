package com.codedbykay.purenotes.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.AddGroupButton
import com.codedbykay.purenotes.components.DrawerContent
import com.codedbykay.purenotes.components.GroupFilterMenu
import com.codedbykay.purenotes.components.GroupSortMenu
import com.codedbykay.purenotes.components.RoundedCircularProgressIndicator
import com.codedbykay.purenotes.components.ToDoGroupList
import com.codedbykay.purenotes.ui.theme.ToDoAppTheme
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        var isInitialized by remember { mutableStateOf(false) }


        LaunchedEffect(Unit) {
            delay(500)
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
                            AddGroupButton(toDoGroupViewModel = toDoGroupViewModel)
                        }
                    )
                }
            ) { paddingValues ->
                if (isInitialized) {
                    Box(modifier = Modifier.padding(paddingValues)) {
                        ToDoGroupList(
                            toDoGroupViewModel = toDoGroupViewModel,
                            onGroupClick = { groupId, groupName ->
                                onNavigateToToDoPage(
                                    groupId,
                                    groupName
                                )
                            }
                        )
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