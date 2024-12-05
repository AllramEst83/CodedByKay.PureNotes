package com.codedbykay.purenotes.ui.theme

import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.constants.ThemeConstants.ThemeMode
import com.codedbykay.purenotes.models.ThemeOption

object ThemeData {
    val themeOptions = listOf(
        ThemeOption(ThemeMode.RED, R.string.red_theme),
        ThemeOption(ThemeMode.PINK, R.string.pink_theme),
        ThemeOption(ThemeMode.SEVENTIES, R.string.seventies_theme),
        ThemeOption(ThemeMode.CARNIVAL, R.string.carnival_theme),
        ThemeOption(ThemeMode.GREEN, R.string.green_theme),
        ThemeOption(ThemeMode.REBECKA_LIGHT, R.string.rebecka_light_theme),
        ThemeOption(ThemeMode.REBECKA_DARK, R.string.rebecka_dark_theme),
        ThemeOption(ThemeMode.SYSTEM_DEFAULT, R.string.system_default)
    )
}