package com.codedbykay.purenotes.workers

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.receivers.ToastReceiver
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson

class RefreshNotesWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override suspend fun doWork(): Result {
        val groupId = inputData.getInt("group_id", -1)
        if (groupId == -1) {
            android.util.Log.e("RefreshNotesWorker", "Invalid group ID")
            return Result.failure()
        }

        val groups: List<ToDoGroup> = toDoGroupDao.getAllGroupsForWorker()
        if (groups.isEmpty()) {
            android.util.Log.e("FetchWidgetDataWorker", "No groups found in the database")
            return Result.failure()
        }

        // Serialize groups to JSON
        val gson = Gson()
        val groupJson = gson.toJson(groups)

        val todos: List<ToDo> = toDoDao.getToDosByGroupIdForWidget(groupId)
        val todosJson = gson.toJson(todos)

        val glanceIds =
            GlanceAppWidgetManager(applicationContext).getGlanceIds(PureNotesWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = applicationContext,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[PureNotesWidget.groupJsonKey] = groupJson
                    this[PureNotesWidget.todoJsonKey] = todosJson
                    this[PureNotesWidget.selectedGroupIdKey] = groupId
                }
            }

            // Trigger widget update
            PureNotesWidget().update(applicationContext, glanceId)
        }

        // Broadcast to show Toast
        val intent = Intent(applicationContext, ToastReceiver::class.java).apply {
            putExtra("toast_message", "Widget data refreshed")
        }
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}

fun scheduleRefreshOfWidgetDataByUserWorker(
    context: Context,
    groupId: Int
) {
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshNotesWorker>()
        .setInputData(
            workDataOf(
                "group_id" to groupId
            )
        )
        .build()

    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
}