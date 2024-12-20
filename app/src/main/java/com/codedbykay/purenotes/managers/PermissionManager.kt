package com.codedbykay.purenotes.managers

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.codedbykay.purenotes.R

class PermissionManager(private val context: Context) {

    fun checkAndRequestNotificationPermission(
        requestPermissionLauncher: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted; proceed as needed
                }

                PackageManager.PERMISSION_DENIED -> {
                    // Request permission
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    fun checkAndRequestExactAlarmPermission() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            if (context is Activity) {
                context.startActivity(intent)
            }
            Toast.makeText(
                context,
                context.getString(R.string.enable_exact_alarms),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun checkAndRequestDndPermission() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            if (context is Activity) {
                context.startActivity(intent)
            }
            Toast.makeText(
                context,
                context.getString(R.string.enable_dnd_access),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun checkAndRequestReadMediaImagesPermission(
        requestPermissionLauncher: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }

                PackageManager.PERMISSION_DENIED -> {
                    // Request permission
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        }
    }
}