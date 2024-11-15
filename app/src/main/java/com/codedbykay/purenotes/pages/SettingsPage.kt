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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.LanguageSelection
import com.codedbykay.purenotes.components.SettingsCard
import com.codedbykay.purenotes.components.ThemeItem
import com.codedbykay.purenotes.ui.theme.ThemeData
import com.codedbykay.purenotes.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(settingsViewModel.getLanguage(context)) }

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
        }
    }
}