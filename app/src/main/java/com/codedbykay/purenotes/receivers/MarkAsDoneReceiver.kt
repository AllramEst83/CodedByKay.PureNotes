package com.codedbykay.purenotes.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkAsDoneReceiver : BroadcastReceiver() {

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()
    private val intentService = MainApplication.IntentService
    private val intentActionName = "com.codedbykay.purenotes.ACTION_MARK_AS_DONE"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == intentActionName) {
            val todoId = intent.getIntExtra("todo_id", -1)
            val title = intent.getStringExtra("notification_title")
                ?: context.getString(R.string.receiver_fallback_title)
            val groupId = intent.getIntExtra("group_id", -1)
            val groupName = intent.getStringExtra("group_title")
                ?: context.getString(R.string.receiver_fallback_group_name)
            if (todoId != -1) {

                // Update the note's status in the database
                CoroutineScope(Dispatchers.IO).launch {

                    toDoDao.updateToDoDone(id = todoId, done = true)

                    val notificationManager = context
                        .getSystemService(
                            Context.NOTIFICATION_SERVICE
                        ) as NotificationManager

                    val formattedTitle: Spanned = Html.fromHtml(
                        "<b>$title</b> ${context.getString(R.string.notification_has_been_completed)}",
                        Html.FROM_HTML_MODE_LEGACY
                    )

                    val pendingIntent = intentService
                        .getMainActivityPendingIntent(
                            todoId,
                            groupId,
                            groupName
                        )

                    val updatedNotification = NotificationCompat.Builder(context, "todo_channel")
                        .setSmallIcon(R.drawable.ic_note_notifications)
                        .setContentTitle(formattedTitle)
                        .setContentText(context.getString(R.string.notification_note_is_marked_as_done))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build()

                    // Use the same notification ID to update the existing notification
                    notificationManager.notify(todoId, updatedNotification)
                }
            }
        }
    }
}
