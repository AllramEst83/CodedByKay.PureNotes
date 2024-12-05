package com.codedbykay.purenotes.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.codedbykay.purenotes.R
import java.util.Calendar

@Composable
fun ShowTimePickerDialog(
    initialDateTime: Calendar = Calendar.getInstance(),
    onDateTimeSelected: (Calendar) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { initialDateTime }

    LaunchedEffect(Unit) {
        // Show DatePickerDialog
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Set the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Show TimePickerDialog
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        // Set the selected time
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        // Get the selected time in milliseconds
                        val selectedTimeInMillis = calendar.timeInMillis

                        // Check if the selected time is in the future
                        if (selectedTimeInMillis > System.currentTimeMillis()) {
                            // Proceed with the valid time
                            onDateTimeSelected(calendar) // Return the combined date and time
                        } else {
                            // Show a message to the user if the time is in the past
                            Toast.makeText(
                                context,
                                context.getString(R.string.select_future_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
