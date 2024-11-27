package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codedbykay.purenotes.workers.MarkToDoAsDoneWorker

class MarkToDoAsDone : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        var todoId: Int? = null
        var groupId: Int? = null

        todoId = parameters[todoIdkey]
        groupId = parameters[groupIdkey]

        if (todoId == null && groupId == null) {
            android.util.Log.e("MarkToDoAsDone", "todoId is missing")
            return
        }

        val workRequest = OneTimeWorkRequestBuilder<MarkToDoAsDoneWorker>()
            .setInputData(
                workDataOf(
                    "todo_id" to todoId,
                    "group_id" to groupId
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    companion object {
        val todoIdkey = ActionParameters.Key<Int>("todo_id")
        val groupIdkey = ActionParameters.Key<Int>("group_Id")
    }
}
