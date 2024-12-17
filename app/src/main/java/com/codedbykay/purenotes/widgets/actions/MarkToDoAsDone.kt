package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.ToDo
import com.codedbykay.purenotes.db.ToDoDao
import com.codedbykay.purenotes.receivers.ToastReceiver
import com.codedbykay.purenotes.services.NotificationService
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkToDoAsDone : ActionCallback, KoinComponent {
    private val toDoDao: ToDoDao by inject()
    private val notificationService: NotificationService by inject()

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
                putExtra("toast_message", context.getString(R.string.error_todo_id_missing))
            }
            context.sendBroadcast(intent)
            return
        }

        toDoDao.updateToDoDone(todoId, true)
        val toDo = toDoDao.getToDoById(todoId)

        if (toDo.notificationRequestCode != null &&
            toDo.notificationAction != null &&
            toDo.notificationDataUri != null
        ) {
            notificationService.cancelNotification(
                toDo.id,
                toDo.notificationRequestCode,
                toDo.notificationAction,
                toDo.notificationDataUri
            )
        }

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
            putExtra(
                "toast_message",
                context.getString(R.string.notification_note_is_marked_as_done)
            )
        }
        context.sendBroadcast(intent)
    }

    companion object {
        val todoIdkey = ActionParameters.Key<Int>("todo_id")
        val groupIdkey = ActionParameters.Key<Int>("group_Id")
    }
}
