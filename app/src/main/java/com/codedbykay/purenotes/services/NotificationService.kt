package com.codedbykay.purenotes.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.receivers.NotificationReceiver

class NotificationService(context: Context) {

    private val appContext = context.applicationContext
    private val channelId = "todo_channel"
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Todo Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for todo items"
            }
            val notificationManager = appContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(
        id: Int,
        title: String,
        groupId: Int,
        description: String,
        timeInMillis: Long
    ) {
        val notificationIntent = Intent(appContext, NotificationReceiver::class.java).apply {
            action = "com.codedbykay.purenotes.ACTION_NOTIFY_$id" // Unique action
            data = Uri.parse("purenotes://todo/$id") // Unique data URI
            putExtra("notification_title", title)
            putExtra("notification_description", description)
            putExtra("todo_id", id)
            putExtra("group_id", groupId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        toDoDao.setAlarmForToDo(
            id = id,
            notificationRequestCode = id,
            notificationAction = notificationIntent.action,
            notificationDataUri = notificationIntent.data.toString(),
            notificationTime = timeInMillis
        )

        // Schedule the exact alarm for Doze Mode compatibility
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }


    suspend fun cancelNotification(
        id: Int,
        notificationRequestCode: Int?,
        notificationAction: String?,
        notificationDataUri: String?
    ) {
        if (notificationRequestCode == null || notificationAction == null || notificationDataUri == null) {
            Log.d("notificationService", "Invalid parameters: Unable to cancel notification")
            return
        }

        val notificationIntent = Intent(appContext, NotificationReceiver::class.java).apply {
            action = notificationAction
            data = Uri.parse(notificationDataUri)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            notificationRequestCode,
            notificationIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("notificationService", "Notification canceled and database updated for ID: $id")
        } else {
            Log.d("notificationService", "Pending Intent does not exist for ID: $id")
        }

        toDoDao.removeAlarmFromToDo(id)
        Log.d("notificationService", "Database updated for ID: $id")
    }
}
