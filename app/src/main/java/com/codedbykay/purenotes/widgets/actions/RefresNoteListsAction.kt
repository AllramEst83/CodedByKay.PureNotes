package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.codedbykay.purenotes.workers.scheduleRefreshOfWidgetDataByUserWorker

class RefresNoteListsAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        var groupId: Int? = null

        groupId = parameters[groupIdKey]

        if (groupId == null) {
            android.util.Log.e("DropdownItemClickAction", "Group id is null. Exiting action.")
            return
        }

        scheduleRefreshOfWidgetDataByUserWorker(
            context,
            groupId
        )
    }

    companion object {

        val groupIdKey = ActionParameters.Key<Int>("group_id")
    }
}