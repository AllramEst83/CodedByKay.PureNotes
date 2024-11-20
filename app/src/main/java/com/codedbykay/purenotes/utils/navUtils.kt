package com.codedbykay.purenotes.utils

import androidx.navigation.NavHostController
import com.codedbykay.purenotes.models.Screen

fun handleEmptyBackNavigation(
    navController: NavHostController,
    fallbackRoute: String = Screen.TODOGROUPS.name
) {
    if (!navController.popBackStack()) {
        // Back stack is empty; navigate to the fallback route
        navController.navigate(fallbackRoute) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}
