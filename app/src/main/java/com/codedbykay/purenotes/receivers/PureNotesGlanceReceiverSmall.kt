package com.codedbykay.purenotes.receivers

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.codedbykay.purenotes.widgets.PureNotesWidget
import com.codedbykay.purenotes.workers.scheduleTFetchWidgetDataOnStartupWorker

class PureNotesGlanceReceiverSmall : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = PureNotesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        scheduleTFetchWidgetDataOnStartupWorker(context)
    }
}
