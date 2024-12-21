package com.codedbykay.purenotes

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.codedbykay.purenotes.managers.AppNavHostManager
import com.codedbykay.purenotes.managers.LocaleManager
import com.codedbykay.purenotes.managers.PermissionManager
import com.codedbykay.purenotes.ui.theme.ToDoAppTheme
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    private val mainScope = getKoin().createScope<MainActivity>()
    private val permissionManager: PermissionManager by mainScope.inject { parametersOf(this) }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this,
                getString(R.string.notification_permission_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.notification_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val requestReadMediaImagesPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this,
                getString(R.string.media_images_permission_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.media_images_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleManager(this).initializeAppLanguage()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Refactored permission requests
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.checkAndRequestNotificationPermission(
                requestNotificationPermissionLauncher
            )
            permissionManager.checkAndRequestReadMediaImagesPermission(
                requestReadMediaImagesPermissionLauncher
            )
        }
        permissionManager.checkAndRequestExactAlarmPermission()
        permissionManager.checkAndRequestDndPermission()

        val toDoGroupViewModel = getViewModel<ToDoGroupViewModel>()
        val toDoViewModel = getViewModel<ToDoViewModel>()
        val imageGalleryViewModel = getViewModel<ImageGalleryViewModel>()
        val settingsViewModel = getViewModel<SettingsViewModel>()

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

    override fun onDestroy() {
        super.onDestroy()
        mainScope.close()
    }
}
