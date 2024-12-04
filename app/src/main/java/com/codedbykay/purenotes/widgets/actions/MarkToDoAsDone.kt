package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.receivers.ToastReceiver
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class MarkToDoAsDone : ActionCallback {
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        var todoId = parameters[todoIdkey]
        var groupId = parameters[groupIdkey]

        if (todoId == null || groupId == null) {
            android.util.Log.e("MarkToDoAsDone", "todoId is missing")

            // Broadcast to show Toast
            val intent = Intent(context, ToastReceiver::class.java).apply {
                putExtra("toast_message", "Failed to mark note as done")
            }
            context.sendBroadcast(intent)
            return
        }

        toDoDao.updateToDoDone(todoId, true)
        val todos = toDoDao.getToDosNotDoneByGroupId(groupId)

        val gson = Gson()
        val groupListType = object : TypeToken<List<ToDo>>() {}.type
        val groupJson = gson.toJson(todos, groupListType)

        // Update widget state
        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[PureNotesWidget.todoJsonKey] = groupJson
            }
        }

        PureNotesWidget().update(context, glanceId)

        // Broadcast to show Toast
        val intent = Intent(context, ToastReceiver::class.java).apply {
            putExtra("toast_message", "Note marked as done")
        }
        context.sendBroadcast(intent)
    }

    companion object {
        val todoIdkey = ActionParameters.Key<Int>("todo_id")
        val groupIdkey = ActionParameters.Key<Int>("group_Id")
    }
}
