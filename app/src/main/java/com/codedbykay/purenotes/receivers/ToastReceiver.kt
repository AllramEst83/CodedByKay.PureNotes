package com.codedbykay.purenotes.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("toast_message") ?: "Action completed"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
