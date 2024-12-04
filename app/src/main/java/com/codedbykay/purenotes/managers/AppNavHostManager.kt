package com.codedbykay.purenotes.managers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.models.NavigationItem
import com.codedbykay.purenotes.models.Screen
import com.codedbykay.purenotes.pages.SettingsPage
import com.codedbykay.purenotes.pages.ToDoGroupPage
import com.codedbykay.purenotes.pages.ToDoPage
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel

@Composable
fun AppNavHostManager(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.ToDoGroups.route,
    toDoGroupViewModel: ToDoGroupViewModel,
    toDoViewModel: ToDoViewModel,
    imageGalleryViewModel: ImageGalleryViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val appPackage = stringResource(id = R.string.app_package)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        // ToDoGroup list screen (start screen)
        composable(route = Screen.TODOGROUPS.name) {
            ToDoGroupPage(
                toDoGroupViewModel = toDoGroupViewModel,
                imageGalleryViewModel = imageGalleryViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.SETTINGS.name)
                },
                onNavigateToToDoPage = { groupId, groupName ->
                    navController.navigate("${Screen.TODO.name}/$groupId/$groupName")
                }
            )
        }

        // ToDoPage for a specific group
        composable(
            route = "${Screen.TODO.name}/{groupId}/{groupName}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType },
                navArgument("groupName") { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink {
                uriPattern =
                    "${appPackage}://todo_list/{groupId}/{groupName}"
            })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId")
            val groupName = backStackEntry.arguments?.getString("groupName")

            if (groupId != null && groupName != null) {
                ToDoPage(
                    toDoViewModel = toDoViewModel,
                    imageGalleryViewModel = imageGalleryViewModel,
                    groupId = groupId,
                    groupName = groupName,
                    navController = navController
                )
            }
        }

        // Theme settings screen
        composable(route = Screen.SETTINGS.name) {
            SettingsPage(
                settingsViewModel = settingsViewModel,
                navController = navController
            )
        }
    }
}