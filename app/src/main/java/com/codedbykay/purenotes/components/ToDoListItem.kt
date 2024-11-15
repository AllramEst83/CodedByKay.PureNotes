package com.codedbykay.purenotes.components

import LoadingItem
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.dialogs.ShowTimePickerDialog
import com.codedbykay.purenotes.utils.customCircleBackground
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun ToDoListItem(
    toDoItem: ToDo,
    toDoViewModel: ToDoViewModel,
    onDelete: () -> Unit,
    onUpdate: (ToDo) -> Unit,
    onDoneUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    toDoItem?.let { nonNullItem ->
        val formattedDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(nonNullItem.createdAt)
        var isEditing by remember { mutableStateOf(false) }
        var title by remember(nonNullItem.id) { mutableStateOf(nonNullItem.title) }
        var content by remember(nonNullItem.id) { mutableStateOf(nonNullItem.content ?: "") }
        var isChecked by remember(nonNullItem.id) { mutableStateOf(nonNullItem.done) }
        var expanded by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val activity = LocalContext.current as? Activity
        var showSettingsDialog by remember { mutableStateOf(false) }

        val rotation by animateFloatAsState(
            targetValue = if (expanded) 0f else 180f,
            label = "ExpandCollapseRotation"
        )

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Permission granted, show time picker
                showTimePicker = true
            } else {
                // Permission denied
                val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == true

                if (shouldShowRationale) {
                    // Permission denied, but we can ask again
                    // Optionally inform the user
                    Toast.makeText(
                        context,
                        "Notification permission is required to set reminders.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Permission denied permanently or 'Don't ask again' selected
                    showSettingsDialog = true
                }
            }
        }

        // When the user clicks to set a notification
        val onSetNotificationClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.POST_NOTIFICATIONS
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Permission is granted, show time picker
                        showTimePicker = true
                    }

                    else -> {
                        // Directly request the permission again
                        requestPermissionLauncher.launch(permission)
                    }
                }
            } else {
                // For lower API levels, show time picker
                showTimePicker = true
            }
        }

        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (!isChecked) 8.dp else 0.dp
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoundCheckbox(
                                    isChecked = isChecked,
                                    onCheckedChange = { newCheckedState ->
                                        // Check if the item is transitioning from unchecked to checked
                                        if (!isChecked && newCheckedState) {
                                            // Check if the item has an alarm set
                                            if (nonNullItem.notificationRequestCode != null &&
                                                nonNullItem.notificationAction != null &&
                                                nonNullItem.notificationDataUri != null
                                            ) {
                                                // Invoke the function to remove the alarm
                                                toDoViewModel.removeAlarmFromToDo(
                                                    nonNullItem.id,
                                                    nonNullItem.notificationRequestCode,
                                                    nonNullItem.notificationAction,
                                                    nonNullItem.notificationDataUri
                                                )
                                            }
                                        }
                                        // Update the checked state
                                        isChecked = newCheckedState
                                        // Call the function to handle the done state update
                                        onDoneUpdate(isChecked)
                                    },
                                    modifier = Modifier


                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DisplayDate(
                                        modifier = Modifier
                                            .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                                            .padding(horizontal = 8.dp, vertical = 1.dp)
                                            .alpha(if (isChecked) 0.5f else 1f),
                                        formattedDate = formattedDate,
                                        isChecked = isChecked
                                    )

                                    // Add a Spacer to create the desired space between the date and icon
                                    Spacer(modifier = Modifier.width(8.dp))

                                    nonNullItem.notificationTime?.let {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                                .alpha(if (isChecked) 0.5f else 1f)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.note_notifications),
                                                contentDescription = "Notification set.",
                                                modifier = Modifier
                                                    .size(19.dp)
                                                    .padding(end = 4.dp)
                                            )
                                            Text(
                                                text = SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm",
                                                    Locale.ENGLISH
                                                )
                                                    .format(
                                                        Date(
                                                            nonNullItem.notificationTime ?: 0L
                                                        )
                                                    ),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                }

                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            EditableTitleField(
                                editModifier = Modifier
                                    .fillMaxWidth()

                                    .alpha(if (isChecked) 0.5f else 1f),
                                textModifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)

                                    .alpha(if (isChecked) 0.5f else 1f),
                                title = title,
                                onTitleChange = { title = it },
                                isEditing = isEditing,
                                isChecked = isChecked

                            )

                            if (expanded) {

                                Spacer(modifier = Modifier.height(4.dp))

                                EditableContentField(
                                    editModifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(if (isChecked) 0.5f else 1f),
                                    textModifier = Modifier
                                        .alpha(if (isChecked) 0.5f else 1f)
                                        .fillMaxWidth()
                                        .padding(start = 8.dp),
                                    content = content,
                                    onContentChange = { content = it },
                                    isEditing = isEditing,
                                    isChecked = isChecked
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (showTimePicker) {
                                    val calendar = Calendar.getInstance().apply {
                                        timeZone = TimeZone.getDefault()
                                    }

                                    ShowTimePickerDialog(
                                        initialDateTime = calendar,
                                        onDateTimeSelected = { selectedDateTime ->
                                            val setNotificationTime = selectedDateTime.timeInMillis
                                            toDoViewModel.addToDoNotification(
                                                notificationTime = setNotificationTime,
                                                id = nonNullItem.id,
                                                title = nonNullItem.title,
                                                content = nonNullItem.content,
                                                groupId = nonNullItem.groupId
                                                )
                                        }
                                    )
                                    LaunchedEffect(Unit) {
                                        showTimePicker = false
                                    }
                                }

                                if (showSettingsDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showSettingsDialog = false },
                                        title = { Text("Permission Required") },
                                        text = { Text("Notification permission is permanently denied. Please enable it in app settings.") },
                                        confirmButton = {
                                            Button(onClick = {
                                                showSettingsDialog = false
                                                // Open app settings
                                                val intent =
                                                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data = Uri.fromParts(
                                                            "package",
                                                            context.packageName,
                                                            null
                                                        )
                                                    }
                                                context.startActivity(intent)
                                            }) {
                                                Text("Open Settings")
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = { showSettingsDialog = false }) {
                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }


                                Spacer(modifier = Modifier.height(8.dp))
                                // Button row
                                ActionButtonsRow(
                                    isEditing = isEditing,
                                    isChecked = isChecked,
                                    notificationTime = nonNullItem.notificationTime,
                                    onDeleteClick = onDelete,
                                    onEditClick = {
                                        if (isEditing) {
                                            val updatedItem = nonNullItem.copy(
                                                title = title,
                                                content = content,
                                                done = isChecked
                                            )

                                            onUpdate(updatedItem)

                                            isEditing = false
                                        } else {
                                            isEditing = true
                                        }
                                    },
                                    onShowTimePickerClick = { onSetNotificationClick() },
                                    onClearNotificationClick = {
                                        // Handle clearing the notification
                                        toDoViewModel.removeAlarmFromToDo(
                                            nonNullItem.id,
                                            nonNullItem.notificationRequestCode,
                                            nonNullItem.notificationAction,
                                            nonNullItem.notificationDataUri
                                        )
                                    },
                                    rowModifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .alpha(if (isChecked) 0.5f else 1f)
                                )
                            }
                        }
                    }
                }

                ExpandCollapseButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                    rotation = rotation,
                    iconModifier = Modifier.align(Alignment.TopEnd),
                    boxModifier = Modifier
                        .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                        .padding(4.dp)
                        .alpha(if (isChecked) 0.5f else 1f)
                )
            }
        }
    } ?: run {
        // Show loading or fallback UI while item is null
        LoadingItem()
    }
}