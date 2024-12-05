package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.codedbykay.purenotes.constants.LanguageConstants
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun LanguageSelection(
    settingsViewModel: SettingsViewModel,
    currentLanguage: String,
    onLanguageSelected: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val languages = LanguageConstants.languages

    Column(modifier = Modifier.fillMaxWidth()) {
        languages.forEach { (code, name) ->
            LanguageSelectionItem(
                languageName = name,
                isSelected = code == currentLanguage,
                onSelect = {
                    coroutineScope.launch {
                        settingsViewModel.setLocale(code)
                        onLanguageSelected()
                    }
                }
            )
        }
    }
}

