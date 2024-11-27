package com.codedbykay.purenotes.workers

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.receivers.ToastReceiver
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class MarkToDoAsDoneWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()
    override suspend fun doWork(): Result {
        val todoId = inputData.getInt("todo_id", -1)
        val groupId = inputData.getInt("group_id", -1)

        if (todoId == -1 && groupId == -1) {
            android.util.Log.e("MarkToDoAsDoneWorker", "Invalid todo ID")
            return Result.failure()
        }

        toDoDao.updateToDoDone(todoId, true)
        val todos = toDoDao.getToDosByGroupIdForWidget(groupId)
        val gson = Gson()
        val groupListType = object : TypeToken<List<ToDo>>() {}.type
        val groupJson = gson.toJson(todos, groupListType)

        // Update widget state
        val glanceIds = GlanceAppWidgetManager(applicationContext)
            .getGlanceIds(PureNotesWidget::class.java)

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = applicationContext,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[PureNotesWidget.todoJsonKey] = groupJson
                }
            }

            PureNotesWidget().update(applicationContext, glanceId)
        }

        // Broadcast to show Toast
        val intent = Intent(applicationContext, ToastReceiver::class.java).apply {
            putExtra("toast_message", "Note marked as done")
        }
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}