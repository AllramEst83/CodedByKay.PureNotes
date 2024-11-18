package com.codedbykay.purenotes.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.BuildConfig
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.LanguageSelection
import com.codedbykay.purenotes.components.SettingsCard
import com.codedbykay.purenotes.components.SharedKeyInput
import com.codedbykay.purenotes.components.ThemeItem
import com.codedbykay.purenotes.managers.PreferencesHelper
import com.codedbykay.purenotes.services.PubSubService
import com.codedbykay.purenotes.ui.theme.ThemeData
import com.codedbykay.purenotes.utils.getOrCreateDeviceId
import com.codedbykay.purenotes.utils.sanitizeTopicName
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    settingsViewModel: SettingsViewModel,
    toDoViewModel: ToDoViewModel,
    toDoGroupViewModel: ToDoGroupViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(settingsViewModel.getLanguage(context)) }
    val deviceId = getOrCreateDeviceId(context)
    var sharedKey by remember { mutableStateOf("") } // State for shared key
    var isTopicCreated by remember { mutableStateOf(false) } // Track topic creation

    val pubSubService = remember {
        PubSubService(
            projectId = BuildConfig.PROJECT_ID,
            context = context,
            deviceId = deviceId,
            toDoViewModel = toDoViewModel,
            toDoGroupViewModel = toDoGroupViewModel
        )
    }

    // Load saved shared key from preferences
    LaunchedEffect(Unit) {
        PreferencesHelper.getSharedKey(context)?.let { savedKey ->
            sharedKey = savedKey
            isTopicCreated = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                SettingsCard(
                    title = stringResource(id = R.string.select_theme),
                    content = {
                        ThemeData.themeOptions.forEach { themeOption ->
                            ThemeItem(
                                currentTheme = settingsViewModel.themeMode,
                                themeOption = themeOption,
                                onThemeSelected = { selectedTheme ->
                                    settingsViewModel.setTheme(selectedTheme)
                                }
                            )
                        }
                    }
                )
            }

            item {
                SettingsCard(
                    title = stringResource(id = R.string.select_language),
                    content = {
                        LanguageSelection(
                            settingsViewModel = settingsViewModel,
                            currentLanguage = currentLanguage,
                            onLanguageSelected = {
                                currentLanguage = settingsViewModel.getLanguage(context)
                            }
                        )
                    }
                )
            }

            item {

                SettingsCard(
                    title = stringResource(id = R.string.shared_key),
                    content = {
                        SharedKeyInput(
                            onSharedKeyChange = { key -> sharedKey = sanitizeTopicName(key) },
                            onConnectionToggle = { isEnabled ->
                                if (!isEnabled) {
                                    PreferencesHelper.deleteSharedKey(context) // Clear preferences if toggled off
                                }
                            },
                            onSaveClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    isTopicCreated = true
                                    val topicName = pubSubService.createTopic(sharedKey)
                                    pubSubService.createSubscription(topicName, deviceId)
                                    pubSubService.listenToSubscription(deviceId)
                                    PreferencesHelper.saveSharedKey(context, sharedKey)
                                }
                            },
                            onDeleteClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    pubSubService.deleteSubscription(deviceId)
                                    pubSubService.deleteTopic(sharedKey)
                                    PreferencesHelper.deleteSharedKey(context)
                                    isTopicCreated = false
                                    sharedKey = ""
                                }
                            },
                            sharedKey = sharedKey,
                            isTopicCreated = isTopicCreated
                        )
                    }
                )
            }
        }
    }
}