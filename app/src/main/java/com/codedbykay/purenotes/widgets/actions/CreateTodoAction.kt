package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.codedbykay.purenotes.workers.SaveTodoWorker

class CreateTodoAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        var inputText: String? = null
        var groupId: Int? = null

        // Retrieve stored input text and group ID
        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { prefs ->
            groupId = prefs[PureNotesWidget.selectedGroupIdKey]
            prefs
        }

        // TODO: User can not write in input in the widget
        if (groupId == null) {
            android.util.Log.e("CreateTodoAction", "Missing input text or group ID")
            return
        }

        // Trigger the Worker
        val workRequest = OneTimeWorkRequestBuilder<SaveTodoWorker>()
            .setInputData(
                workDataOf(
                    "todo_text" to inputText,
                    "group_id" to groupId
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        android.util.Log.d(
            "CreateTodoAction",
            "Worker triggered with input: $inputText and group ID: $groupId"
        )
    }
}