package com.codedbykay.purenotes.managers

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.codedbykay.purenotes.constants.LanguageConstants
import java.util.Locale

class LocaleManager(private val context: Context) {

    fun initializeAppLanguage() {
        val savedLanguage = getSavedLanguage()
        if (savedLanguage.isBlank()) {
            val systemLanguage = getSystemLanguage()
            val appLanguage = if (isLanguageSupported(systemLanguage)) {
                systemLanguage
            } else {
                "en" // Default to English
            }
            setLanguage(appLanguage)
        } else {
            setLanguage(savedLanguage)
        }
    }

    fun setLanguage(languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }

        saveLanguage(languageCode)
    }

    fun saveLanguage(languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("language_code", languageCode).apply()
    }

    fun getSavedLanguage(): String {
        val sharedPreferences = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language_code", "") ?: ""
    }

    private fun getSystemLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0].language // Gets the top language in the system's locale list
        } else {
            Locale.getDefault().language
        }
    }

    private fun isLanguageSupported(languageCode: String): Boolean {
        return LanguageConstants.languages.map { it.first }.contains(languageCode)
    }

}