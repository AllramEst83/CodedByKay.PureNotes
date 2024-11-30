package com.codedbykay.purenotes.utils

import android.content.Context
import java.util.UUID

// Used with Pub/Sub branched feature
fun getOrCreateDeviceId(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("device_id", null) ?: run {
        val newDeviceId = UUID.randomUUID().toString()
        sharedPreferences.edit().putString("device_id", newDeviceId).apply()
        newDeviceId
    }
}
