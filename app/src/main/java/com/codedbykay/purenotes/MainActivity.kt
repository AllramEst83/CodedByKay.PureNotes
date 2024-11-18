package com.codedbykay.purenotes

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.codedbykay.purenotes.notifications.NotificationHelper
import com.codedbykay.purenotes.pages.SettingsPage
import com.codedbykay.purenotes.pages.ToDoGroupPage
import com.codedbykay.purenotes.pages.ToDoPage
import com.codedbykay.purenotes.services.PubSubService
import com.codedbykay.purenotes.ui.theme.ToDoAppTheme
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel

class MainActivity : ComponentActivity() {

    lateinit var pubSubService: PubSubService
        private set

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with any notification-related setup
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied, handle gracefully
            Toast.makeText(
                this,
                "Notification permission is required for notifications",
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

        val toDoGroupViewModel = ViewModelProvider(this)[ToDoGroupViewModel::class.java]
        val notificationHelper = NotificationHelper(this)
        val toDoViewModel = ToDoViewModel(notificationHelper)
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setContent {
            ToDoAppTheme(settingsViewModel) {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    NavHost(navController = navController, startDestination = "todo_groups") {

                        // ToDoGroup list screen (start screen)
                        composable(route = "todo_groups") {
                            ToDoGroupPage(
                                toDoGroupViewModel = toDoGroupViewModel,
                                settingsViewModel = settingsViewModel,
                                onNavigateToSettings = {
                                    navController.navigate("settings_settings")
                                },
                                onNavigateToToDoPage = { groupId, groupName ->
                                    navController.navigate("todo_list/$groupId/$groupName")
                                }

                            )
                        }

                        // ToDoPage for a specific group
                        composable(
                            route = "todo_list/{groupId}/{groupName}",
                            arguments = listOf(
                                navArgument("groupId") { type = NavType.IntType },
                                navArgument("groupName") { type = NavType.StringType }
                            ),
                            deepLinks = listOf(navDeepLink {
                                uriPattern =
                                    "com.codedbykay.purenotes://todo_list/{groupId}/{groupName}"
                            })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId")
                            val groupName = backStackEntry.arguments?.getString("groupName")

                            if (groupId != null && groupName != null) {
                                ToDoPage(
                                    toDoViewModel = toDoViewModel,
                                    groupId = groupId,
                                    groupName = groupName,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Theme settings screen
                        composable(route = "settings_settings") {
                            SettingsPage(
                                settingsViewModel = settingsViewModel,
                                toDoViewModel = toDoViewModel,
                                toDoGroupViewModel = toDoGroupViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
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
                "Please enable exact alarms for timely notifications.",
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
                "Please allow 'Do Not Disturb' access for better notifications.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}