package com.codedbykay.purenotes.managers

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val PREFS_NAME = "com.codedbykay.purenotes.prefs"
    private const val KEY_SHARED_KEY = "shared_key"

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSharedKey(context: Context, key: String) {
        getPreferences(context).edit()
            .putString(KEY_SHARED_KEY, key)
            .apply()
    }

    fun getSharedKey(context: Context): String? =
        getPreferences(context).getString(KEY_SHARED_KEY, null)

    fun deleteSharedKey(context: Context) {
        getPreferences(context).edit()
            .remove(KEY_SHARED_KEY)
            .apply()
    }
}