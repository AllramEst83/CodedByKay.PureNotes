package com.codedbykay.purenotes.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.services.IntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override fun doWork(): Result {
        val title = inputData.getString("notification_title")
            ?: applicationContext.getString(R.string.notification_fallback_title)
        val description = inputData.getString("notification_description")
            ?: applicationContext.getString(R.string.notification_fallback_description)
        val todoId = inputData.getInt("todo_id", -1)
        val groupId = inputData.getInt("group_id", -1)
        val groupName = toDoDao.getGroupNameById(groupId)

        // Create mark as done confirmation intent for the notification
        val markAsDoneIntent = IntentService.getMarkAsDonePendingIntent(
            applicationContext,
            todoId,
            title,
            groupId,
            groupName
        )

        // Create the pending intent for the notification
        val pendingIntent = IntentService.getMainActivityPendingIntent(
            applicationContext,
            todoId,
            groupId,
            groupName
        )

        // Display the notification
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "todo_channel")
            .setSmallIcon(R.drawable.ic_note_notifications)
            .setColor(ContextCompat.getColor(applicationContext, R.color.background_color))
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_note_notifications,
                applicationContext.getString(R.string.notification_mark_as_done),
                markAsDoneIntent
            )
            .build()

        // Use `todoId` as notification ID for easy reference
        notificationManager.notify(todoId, notification)

        // Update the database to clear notification data
        if (todoId != -1) {

            CoroutineScope(Dispatchers.IO).launch {
                // Set notificationTime and notificationId to null
                toDoDao.removeAlarmFromToDo(todoId)

                // Prune completed work entries from WorkManager database
                WorkManager.getInstance(applicationContext).pruneWork()
                Log.d(
                    "NotificationWorker",
                    "Clearing notificationTime and notificationId for ToDo ID: $todoId"
                )
            }
        }

        return Result.success()
    }
}
