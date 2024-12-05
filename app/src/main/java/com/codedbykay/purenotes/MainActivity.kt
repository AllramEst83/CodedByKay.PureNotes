package com.codedbykay.purenotes

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.codedbykay.purenotes.managers.AppNavHostManager
import com.codedbykay.purenotes.ui.theme.ToDoAppTheme
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with any notification-related setup
            Toast.makeText(
                this,
                getString(R.string.notification_permission_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Permission denied, handle gracefully
            Toast.makeText(
                this,
                getString(R.string.notification_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission()
        }
        checkAndRequestExactAlarmPermission()
        checkAndRequestDndPermission()

        // Initialize ViewModels before setContent
        val toDoGroupViewModel = ViewModelProvider(this)[ToDoGroupViewModel::class.java]
        val toDoViewModel = ViewModelProvider(this)[ToDoViewModel::class.java]
        val imageGalleryViewModel = ViewModelProvider(this)[ImageGalleryViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        setContent {
            ToDoAppTheme(settingsViewModel) {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    AppNavHostManager(
                        navController = navController,
                        toDoGroupViewModel = toDoGroupViewModel,
                        toDoViewModel = toDoViewModel,
                        imageGalleryViewModel = imageGalleryViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.insetsController?.setSystemBarsAppearance(0, 0)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted; proceed as needed
                }

                PackageManager.PERMISSION_DENIED -> {
                    // Request permission
                    requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun checkAndRequestExactAlarmPermission() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Redirect to App Alarms & Reminders Settings
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
            Toast.makeText(
                this,
                getString(R.string.enable_exact_alarms),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkAndRequestDndPermission() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // Direct user to settings to enable DND access
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
            Toast.makeText(
                this,
                getString(R.string.enable_dnd_access),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
