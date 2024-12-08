package com.codedbykay.purenotes

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.codedbykay.purenotes.utils.getOrCreateDeviceId
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class GetOrCreateDeviceIdTest {
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        // Use a real SharedPreferences object for testing
        sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("test_prefs", Context.MODE_PRIVATE)

        // Clear SharedPreferences before each test
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun `getOrCreateDeviceId creates and saves a new device ID if not present`() {
        // Call the function
        val deviceId = getOrCreateDeviceId(sharedPreferences)

        // Verify a new device ID was created and stored
        val storedDeviceId = sharedPreferences.getString("device_id", null)
        assertEquals(deviceId, storedDeviceId)
    }

    @Test
    fun `getOrCreateDeviceId returns existing device ID if present`() {
        // Set an existing device ID
        val existingDeviceId = "existing-device-id"
        sharedPreferences.edit().putString("device_id", existingDeviceId).commit()

        // Call the function
        val deviceId = getOrCreateDeviceId(sharedPreferences)

        // Verify the existing device ID is returned
        assertEquals(existingDeviceId, deviceId)
    }
}