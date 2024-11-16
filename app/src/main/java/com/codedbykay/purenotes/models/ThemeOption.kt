package com.codedbykay.purenotes.models

import androidx.annotation.StringRes
import com.codedbykay.purenotes.viewModels.SettingsViewModel

data class ThemeOption(
    val mode: SettingsViewModel.ThemeMode,
    @StringRes val displayNameRes: Int
)