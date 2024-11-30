package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson

class DropdownItemClickAction : ActionCallback {
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Retrieve the selected index from ActionParameters
        val selectedIndex = parameters[indexKey]
        if (selectedIndex == null) {
            android.util.Log.e("DropdownItemClickAction", "Selected index is null. Exiting action.")
            return
        }

        // Logging: Action triggered
        android.util.Log.d(
            "DropdownItemClickAction",
            "Dropdown item click action triggered. Selected index: $selectedIndex"
        )

        val groupId = parameters[groupIdKey]
        if (groupId == null) {
            android.util.Log.e("DropdownItemClickAction", "Group id is null. Exiting action.")
            return
        }

        // Logging: Action triggered
        android.util.Log.d(
            "DropdownItemClickAction",
            "Dropdown item click action triggered. Group id: $groupId"
        )

        // Update the widget state with new data
        val todos = toDoDao.getToDosByGroupId(groupId)
        // Serialize todos as JSON to store in preferences
        val gson = Gson()
        val todosJson = gson.toJson(todos)

        // Update widget state with the fetched todos
        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[PureNotesWidget.todoJsonKey] = todosJson
                this[PureNotesWidget.selectedIndexKey] = selectedIndex
                this[PureNotesWidget.selectedGroupIdKey] = groupId
                this[PureNotesWidget.isDropDownExpandedKey] = false

                android.util.Log.d(
                    "DropdownItemClickAction",
                    "Updated preferences: selectedIndex=$selectedIndex, dropdownCollapsed=true, selectedGroupIdKey=$groupId"
                )
            }
        }

        PureNotesWidget().update(context, glanceId)

        // Trigger a widget update
        PureNotesWidget().update(context, glanceId)
        android.util.Log.d("DropdownItemClickAction", "Widget updated after item selection.")
    }

    companion object {
        val indexKey = ActionParameters.Key<Int>("selected_index")
        val groupIdKey = ActionParameters.Key<Int>("group_id")
    }
}
