package com.codedbykay.purenotes.workers

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class FetchWidgetDataOnStartupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override suspend fun doWork(): Result {
        // Fetch groups
        val groups: List<ToDoGroup> = toDoGroupDao.getAllGroupsForWorker()

        // Serialize groups to JSON
        val gson = Gson()
        val groupJson = gson.toJson(groups)

        // Take the first group (index 0)
        val firstGroupId = groups.firstOrNull()?.id ?: -1
        if (firstGroupId == -1) {
            android.util.Log.e("FetchWidgetDataWorker", "Failed to determine first group ID")
            return Result.failure()
        }

        // Fetch todos for the first group
        val todos: List<ToDo> = toDoDao.getToDosNotDoneByGroupId(firstGroupId)
        val todosJson = gson.toJson(todos)

        // Update widget state with groups and todos
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
                    this[PureNotesWidget.selectedGroupIdKey] = firstGroupId
                    this[PureNotesWidget.isAppInitializedKey] = true
                }
            }

            // Trigger widget update
            PureNotesWidget().update(applicationContext, glanceId)
        }

        android.util.Log.d("FetchWidgetDataWorker", "Successfully fetched and updated widget data")
        return Result.success()
    }
}

fun scheduleTFetchWidgetDataOnStartupWorker(context: Context) {
    // Trigger a one-time worker to fetch data immediately
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<FetchWidgetDataOnStartupWorker>()
        .setInitialDelay(0, TimeUnit.SECONDS)
        .build()
    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
}