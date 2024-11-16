package com.codedbykay.purenotes.ui.theme

import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.models.ThemeOption
import com.codedbykay.purenotes.viewModels.SettingsViewModel

object ThemeData {
    val themeOptions = listOf(
        ThemeOption(SettingsViewModel.ThemeMode.RED, R.string.red_theme),
        ThemeOption(SettingsViewModel.ThemeMode.PINK, R.string.pink_theme),
        ThemeOption(SettingsViewModel.ThemeMode.SEVENTIES, R.string.seventies_theme),
        ThemeOption(SettingsViewModel.ThemeMode.CARNIVAL, R.string.carnival_theme),
        ThemeOption(SettingsViewModel.ThemeMode.GREEN, R.string.green_theme),
        ThemeOption(SettingsViewModel.ThemeMode.REBECKA_LIGHT, R.string.rebecka_light_theme),
        ThemeOption(SettingsViewModel.ThemeMode.REBECKA_DARK, R.string.rebecka_dark_theme),
        ThemeOption(SettingsViewModel.ThemeMode.SYSTEM_DEFAULT, R.string.system_default)
    )
}