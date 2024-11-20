package com.codedbykay.purenotes.models

sealed class NavigationItem(val route: String) {
    object ToDoGroups : NavigationItem(Screen.TODOGROUPS.name)
    object ToDo : NavigationItem(Screen.TODO.name)
    object Settings : NavigationItem(Screen.SETTINGS.name)
}

enum class Screen {
    TODOGROUPS,
    TODO,
    SETTINGS,
}