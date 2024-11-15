package com.codedbykay.purenotes.viewModels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.managers.LocaleManager
import com.codedbykay.purenotes.managers.ThemeDataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val localeManager = LocaleManager()

    enum class ThemeMode { REBECKA_LIGHT, REBECKA_DARK, RED, GREEN, PINK, SEVENTIES, CARNIVAL, SYSTEM_DEFAULT }

    private val themeDataStoreManager = ThemeDataStoreManager(application)
    var themeMode by mutableStateOf(ThemeMode.SYSTEM_DEFAULT)
        private set

    init {
        loadThemeMode()
    }

    private fun loadThemeMode() {
        viewModelScope.launch {
            themeDataStoreManager.themeMode.collect { savedThemeMode ->
                themeMode = savedThemeMode // Update the UI when the theme mode changes
            }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        this.themeMode = themeMode
        viewModelScope.launch(Dispatchers.IO) {
            themeDataStoreManager.saveThemeMode(themeMode)
        }
    }

    fun setLocale(context: Context, locale: String) {
        localeManager.setLanguage(context, locale)
    }

    fun getLanguage(context: Context): String {
        return localeManager.getSavedLanguage(context)

    }
}
