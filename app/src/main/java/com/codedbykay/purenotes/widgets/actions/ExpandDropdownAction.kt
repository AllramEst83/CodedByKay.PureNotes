package com.codedbykay.purenotes.widgets.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.codedbykay.purenotes.widgets.PureNotesWidget

class ExpandDropdownAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        android.util.Log.d("ExpandDropdownAction", "Dropdown toggle action triggered.")
        // Retrieve the current expanded state from Preferences and toggle it
        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { preferences ->
            val isExpanded = preferences[PureNotesWidget.isDropDownExpandedKey] == true

            android.util.Log.d(
                "ExpandDropdownAction",
                "Current dropdown state: $isExpanded. Toggling state."
            )

            preferences.toMutablePreferences().apply {
                this[PureNotesWidget.isDropDownExpandedKey] = !isExpanded
            }
        }

        // Trigger a widget update
        PureNotesWidget().update(context, glanceId)
        android.util.Log.d("ExpandDropdownAction", "Widget updated after toggling dropdown.")
    }
}