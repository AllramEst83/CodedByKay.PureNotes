package com.codedbykay.purenotes.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.codedbykay.purenotes.utils.adjustedForStatusBar
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.SettingsViewModel.ThemeMode

@Composable
fun ToDoAppTheme(
    settingsViewModel: SettingsViewModel,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Take theme from phone settings
    content: @Composable () -> Unit
) {
    val themeMode = settingsViewModel.themeMode

    val colorScheme = when (themeMode) {
        ThemeMode.RED -> redColorScheme
        ThemeMode.GREEN -> greenColorScheme
        ThemeMode.PINK -> pinkColorScheme
        ThemeMode.SEVENTIES -> seventiesColorScheme
        ThemeMode.CARNIVAL -> carnivalColorScheme
        ThemeMode.REBECKA_LIGHT -> lightSchemeRebecka
        ThemeMode.REBECKA_DARK -> darkSchemeRebecka

        ThemeMode.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) darkSchemeDefaultDark else lightSchemeDefaultLight
        else -> lightSchemeDefaultLight
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val navigationBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            colorScheme.background.toArgb()
        } else {
            Color(0x50000000).toArgb() // Semi-transparent black
        }

        val statusBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            colorScheme.background.adjustedForStatusBar(0.4f).toArgb()
        } else {
            Color(0x50000000).toArgb() // Semi-transparent black
        }

        SideEffect {
            setUpEdgeToEdge(view, darkTheme, navigationBarColor, statusBarColor)
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}


private fun setUpEdgeToEdge(
    view: View,
    darkTheme: Boolean,
    navigationBarColor: Int,
    statusBarColor: Int
) {
    val window = (view.context as Activity).window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.navigationBarColor = navigationBarColor
    window.statusBarColor = statusBarColor
    val controller = WindowCompat.getInsetsController(window, view)
    controller.isAppearanceLightStatusBars = !darkTheme
    controller.isAppearanceLightNavigationBars = !darkTheme
}