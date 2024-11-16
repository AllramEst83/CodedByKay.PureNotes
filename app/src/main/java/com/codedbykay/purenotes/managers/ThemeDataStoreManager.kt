package com.codedbykay.purenotes.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemeDataStoreManager(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    // Function to save the theme mode using enum
    suspend fun saveThemeMode(themeMode: SettingsViewModel.ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    // Function to retrieve the theme mode as a Flow and convert to enum
    val themeMode: Flow<SettingsViewModel.ThemeMode> = context.themeDataStore.data
        .map { preferences ->
            val themeString =
                preferences[THEME_MODE_KEY] ?: SettingsViewModel.ThemeMode.SYSTEM_DEFAULT.name
            SettingsViewModel.ThemeMode.valueOf(themeString)
        }
}