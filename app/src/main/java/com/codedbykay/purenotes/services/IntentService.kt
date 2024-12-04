package com.codedbykay.purenotes.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.codedbykay.purenotes.MainActivity
import com.codedbykay.purenotes.receivers.MarkAsDoneReceiver

class IntentService(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private const val ACTION_MARK_AS_DONE = "com.codedbykay.purenotes.ACTION_MARK_AS_DONE"
    }

    /**
     * Creates a PendingIntent for marking a ToDo item as done.
     *
     * @param context The application context.
     * @param todoId The unique identifier of the ToDo item.
     * @return A PendingIntent that triggers the MarkAsDoneReceiver.
     */
    fun getMarkAsDonePendingIntent(
        todoId: Int,
        title: String,
        groupId: Int,
        groupName: String
    ): PendingIntent {
        val markAsDoneIntent = Intent(appContext, MarkAsDoneReceiver::class.java).apply {
            action = ACTION_MARK_AS_DONE
            putExtra("todo_id", todoId)
            putExtra("notification_title", title)
            putExtra("group_id", groupId)
            putExtra("group_title", groupName)

        }
        return PendingIntent.getBroadcast(
            appContext,
            todoId,
            markAsDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a PendingIntent for navigating to a specific ToDo list in the MainActivity.
     *
     * @param context The application context.
     * @param todoId The unique identifier of the ToDo item.
     * @param groupId The unique identifier of the group.
     * @param groupName The name of the group.
     * @return A PendingIntent that opens the MainActivity with the specified deep link.
     */
    fun getMainActivityPendingIntent(
        todoId: Int,
        groupId: Int,
        groupName: String
    ): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("com.codedbykay.purenotes://todo_list/$groupId/$groupName")
        }
        return PendingIntent.getActivity(
            appContext,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
