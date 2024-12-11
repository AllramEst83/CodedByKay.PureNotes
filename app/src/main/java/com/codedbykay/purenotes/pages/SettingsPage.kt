package com.codedbykay.purenotes.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.LanguageSelection
import com.codedbykay.purenotes.components.RoundedCircularProgressIndicator
import com.codedbykay.purenotes.components.SettingsCard
import com.codedbykay.purenotes.components.ThemeItem
import com.codedbykay.purenotes.components.ThemePreview
import com.codedbykay.purenotes.ui.theme.ThemeData
import com.codedbykay.purenotes.utils.handleEmptyBackNavigation
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    var currentLanguage by remember { mutableStateOf(settingsViewModel.getLanguage()) }
    var backButtonEnabled by remember { mutableStateOf(true) }
    var isInitialized by remember { mutableStateOf(false) }

    // Debounce logic
    LaunchedEffect(backButtonEnabled) {
        delay(200)
        if (!backButtonEnabled) {
            delay(500)
            backButtonEnabled = true
        }

        isInitialized = true
    }

    // BackHandler to manage system back presses
    BackHandler(enabled = backButtonEnabled) {
        backButtonEnabled = false
        handleEmptyBackNavigation(navController)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            handleEmptyBackNavigation(navController)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isInitialized) {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.select_theme),
                        content = {
                            ThemePreview()
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
                                    currentLanguage = settingsViewModel.getLanguage()
                                }
                            )
                        }
                    )
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