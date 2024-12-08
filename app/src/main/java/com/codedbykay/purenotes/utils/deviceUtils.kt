package com.codedbykay.purenotes.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

// Used with Pub/Sub branched feature
fun getOrCreateDeviceId(sharedPreferences: SharedPreferences): String {
    return sharedPreferences.getString("device_id", null) ?: run {
        val newDeviceId = UUID.randomUUID().toString()
        sharedPreferences.edit().putString("device_id", newDeviceId).apply()
        newDeviceId
    }
}

