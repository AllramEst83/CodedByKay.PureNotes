package com.codedbykay.purenotes.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    // Locale manager
    private val localeManager = MainApplication.localeManager

    enum class ThemeMode { REBECKA_LIGHT, REBECKA_DARK, RED, GREEN, PINK, SEVENTIES, CARNIVAL, SYSTEM_DEFAULT }

    private val themeDataStoreManager = MainApplication.themeDataStoreManager
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

    fun setLocale(locale: String) {
        localeManager.setLanguage(locale)
    }

    fun getLanguage(): String {
        return localeManager.getSavedLanguage()

    }
}
