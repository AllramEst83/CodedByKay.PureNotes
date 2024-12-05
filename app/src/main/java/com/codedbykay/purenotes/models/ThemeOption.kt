package com.codedbykay.purenotes.models

import androidx.annotation.StringRes
import com.codedbykay.purenotes.constants.ThemeConstants.ThemeMode

data class ThemeOption(
    val mode: ThemeMode,
    @StringRes val displayNameRes: Int,
)