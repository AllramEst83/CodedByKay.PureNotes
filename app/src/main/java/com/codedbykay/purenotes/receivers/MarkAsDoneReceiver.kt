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
import com.codedbykay.purenotes.services.IntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkAsDoneReceiver : BroadcastReceiver() {

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.codedbykay.purenotes.ACTION_MARK_AS_DONE") {
            val todoId = intent.getIntExtra("todo_id", -1)
            val title = intent.getStringExtra("notification_title") ?: "It's done!"
            val groupId = intent.getIntExtra("group_id", -1)
            val groupName = intent.getStringExtra("group_title") ?: "Group name"
            if (todoId != -1) {

                // Update the note's status in the database
                CoroutineScope(Dispatchers.IO).launch {

                    toDoDao.updateToDoDone(id = todoId, done = true)

                    val notificationManager = context
                        .getSystemService(
                            Context.NOTIFICATION_SERVICE
                        ) as NotificationManager

                    val formattedTitle: Spanned = Html.fromHtml(
                        "<b>$title</b> has been Completed",
                        Html.FROM_HTML_MODE_LEGACY
                    )

                    val pendingIntent = IntentService
                        .getMainActivityPendingIntent(
                            context,
                            todoId,
                            groupId,
                            groupName
                        )

                    val updatedNotification = NotificationCompat.Builder(context, "todo_channel")
                        .setSmallIcon(R.drawable.ic_note_notifications)
                        .setContentTitle(formattedTitle)
                        .setContentText("The note has been marked as done.")
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
