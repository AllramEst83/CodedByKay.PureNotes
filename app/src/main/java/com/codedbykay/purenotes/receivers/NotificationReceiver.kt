package com.codedbykay.purenotes.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.services.UUIDService
import com.codedbykay.purenotes.workers.NotificationWorker

class NotificationReceiver : BroadcastReceiver() {

    private val intentActionName = "com.codedbykay.purenotes.ACTION_NOTIFY"

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todo_id", -1)
        if (intent.action == "${intentActionName}_${todoId}") {

            val title = intent.getStringExtra("notification_title")
                ?: context.getString(R.string.notification_fallback_title)
            val description = intent.getStringExtra("notification_description")
                ?: context.getString(R.string.notification_fallback_description)
            val todoId = intent.getIntExtra("todo_id", -1)
            val groupId = intent.getIntExtra("group_id", -1)

            val data = workDataOf(
                "notification_title" to title,
                "notification_description" to description,
                "todo_id" to todoId,
                "group_id" to groupId
            )

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UUIDService().generateUUID().toString(),
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
