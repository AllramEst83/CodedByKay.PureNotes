package com.codedbykay.purenotes.workers

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class FetchToDosByGroupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()
    override suspend fun doWork(): Result {
        val groupId = inputData.getInt("group_id", -1)
        if (groupId == -1) {
            android.util.Log.e("FetchTodosWorker", "Invalid group ID")
            return Result.failure()
        }

        // Simulate fetching todos from a database
        val todos = toDoDao.getToDosByGroupIdForWidget(groupId)
        // Serialize todos as JSON to store in preferences
        val gson = Gson()
        val todosJson = gson.toJson(todos)

        // Update widget state with the fetched todos
        val glanceIds =
            GlanceAppWidgetManager(applicationContext).getGlanceIds(PureNotesWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = applicationContext,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[PureNotesWidget.todoJsonKey] = todosJson
                }
            }

            PureNotesWidget().update(applicationContext, glanceId)
        }

        return Result.success()
    }

}

fun scheduleOneTimeToDosFetchWorker(context: Context, groupId: Int) {
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<FetchToDosByGroupWorker>()
        .setInitialDelay(0, TimeUnit.SECONDS)
        .setInputData(
            workDataOf("group_id" to groupId) // Pass groupId as input data
        )
        .build()
    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
}