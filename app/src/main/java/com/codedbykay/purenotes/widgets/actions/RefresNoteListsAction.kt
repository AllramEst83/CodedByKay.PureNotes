package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.ToDo
import com.codedbykay.purenotes.db.ToDoGroup
import com.codedbykay.purenotes.receivers.ToastReceiver
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson

class RefreshNoteListsAction : ActionCallback {
    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        var groupId = parameters[groupIdKey] ?: run {
            // If groupId is null, try to get it from current widget state
            val prefs = getAppWidgetState(
                context = context,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            )
            prefs[PureNotesWidget.selectedGroupIdKey]
        }

        if (groupId == null) {
            android.util.Log.e("RefreshNotesAction", "Group id is null. Exiting action.")

            // Broadcast to show Toast
            val intent = Intent(context, ToastReceiver::class.java).apply {
                putExtra("toast_message", context.getString(R.string.error_group_id_missing))
            }
            context.sendBroadcast(intent)
            return
        }

        // Get all groups
        val groups: List<ToDoGroup> = toDoGroupDao.getAllGroupsForWorker()

        // Find the correct index for the current groupId
        val selectedIndex = groups.indexOfFirst { it.id == groupId }.takeIf { it != -1 } ?: 0

        // Update groupId to the id of the group at selectedIndex
        groupId = groups.getOrNull(selectedIndex)?.id ?: run {
            // No groups available, handle this case
            android.util.Log.e("RefreshNotesAction", "No groups available")
            // Clear the widget data or display a message
            val gson = Gson()
            val groupJson = gson.toJson(emptyList<ToDoGroup>())
            val todosJson = gson.toJson(emptyList<ToDo>())

            updateAppWidgetState(
                context = context,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[PureNotesWidget.groupJsonKey] = groupJson
                    this[PureNotesWidget.todoJsonKey] = todosJson
                    this[PureNotesWidget.selectedGroupIdKey] = -1
                    this[PureNotesWidget.selectedIndexKey] = 0
                    this[PureNotesWidget.isDropDownExpandedKey] = false
                }
            }

            // Trigger widget update
            PureNotesWidget().update(context, glanceId)

            // Broadcast to show Toast
            val intent = Intent(context, ToastReceiver::class.java).apply {
                putExtra("toast_message", context.getString(R.string.error_no_groups_available))
            }
            context.sendBroadcast(intent)
            return
        }

        // Serialize groups to JSON
        val gson = Gson()
        val groupJson = gson.toJson(groups)

        val todos: List<ToDo> = toDoDao.getToDosNotDoneByGroupId(groupId)
        val todosJson = gson.toJson(todos)

        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[PureNotesWidget.groupJsonKey] = groupJson
                this[PureNotesWidget.todoJsonKey] = todosJson
                this[PureNotesWidget.selectedGroupIdKey] = groupId
                this[PureNotesWidget.selectedIndexKey] = selectedIndex
                this[PureNotesWidget.isDropDownExpandedKey] = false
            }
        }

        // Trigger widget update
        PureNotesWidget().update(context, glanceId)

        // Broadcast to show Toast
        val intent = Intent(context, ToastReceiver::class.java).apply {
            putExtra("toast_message", context.getString(R.string.widget_data_refreshed))
        }
        context.sendBroadcast(intent)
    }


    companion object {

        val groupIdKey = ActionParameters.Key<Int>("group_id")
    }
}