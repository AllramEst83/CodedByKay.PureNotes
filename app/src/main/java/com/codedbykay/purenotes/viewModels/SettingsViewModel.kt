package com.codedbykay.purenotes.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.constants.ThemeConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    // Locale manager
    private val localeManager = MainApplication.localeManager

    private val themeDataStoreManager = MainApplication.themeDataStoreManager
    var themeMode by mutableStateOf(ThemeConstants.ThemeMode.SYSTEM_DEFAULT)
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

    fun setTheme(themeMode: ThemeConstants.ThemeMode) {
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
