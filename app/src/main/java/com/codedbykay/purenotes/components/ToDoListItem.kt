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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.ToDo
import com.codedbykay.purenotes.dialogs.ShowTimePickerDialog
import com.codedbykay.purenotes.utils.customCircleBackground
import com.codedbykay.purenotes.utils.formatToString
import com.codedbykay.purenotes.utils.toFormattedDate
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import java.util.Calendar
import java.util.TimeZone

@Composable
fun ToDoListItem(
    toDoItem: ToDo?,
    toDoViewModel: ToDoViewModel,
    imageGalleryViewModel: ImageGalleryViewModel,
    onDelete: () -> Unit,
    onUpdate: (ToDo) -> Unit,
    onDoneUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    maxImages: Int = 10,
) {

    toDoItem?.let { nonNullItem ->
        val formattedCreateDate = nonNullItem.createdAt.formatToString()
        val formattedNotificationDate = nonNullItem.notificationTime?.toFormattedDate()
        var isEditing by remember { mutableStateOf(false) }
        var title by remember(nonNullItem.id) { mutableStateOf(nonNullItem.title) }
        var isChecked by remember(nonNullItem.id) { mutableStateOf(nonNullItem.done) }
        var expanded by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val activity = LocalContext.current as? Activity
        var showSettingsDialog by remember { mutableStateOf(false) }
        val clipboardManager = LocalClipboardManager.current
        val images by imageGalleryViewModel.getImageByToDoId(nonNullItem.id)
            .observeAsState(emptyList())
        var content by remember(nonNullItem.id) {
            mutableStateOf(nonNullItem.content ?: "")
        }

        val displayContent = if (content.isEmpty()) {
            stringResource(R.string.notification_boom)
        } else {
            content
        }

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
                        context.getString(R.string.notification_permission_message),
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
                                            .padding(horizontal = 11.dp, vertical = 6.dp)
                                            .alpha(if (isChecked) 0.5f else 1f),
                                        formattedDate = formattedCreateDate,
                                        isChecked = isChecked
                                    )

                                    // Add a Spacer to create the desired space between the date and icon
                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Notification time icon and text
                                    nonNullItem.notificationTime?.let {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                                                .padding(horizontal = 11.dp, vertical = 6.dp)
                                                .alpha(if (isChecked) 0.5f else 1f)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_note_notifications),
                                                contentDescription = "Notification set.",
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(end = 4.dp)
                                            )
                                            Text(
                                                text = formattedNotificationDate ?: "N/A",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.labelMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                }

                            }

                            Spacer(modifier = Modifier.height(10.dp))

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

                            if ((!expanded && (content.isNotEmpty() || images.isNotEmpty()))) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "More content available",
                                    modifier = Modifier
                                        .padding(start = 10.dp, top = 4.dp)
                                        .size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }

                            if (expanded) {

                                Spacer(modifier = Modifier.height(8.dp))

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

                                ImageGallery(
                                    images = images,
                                    maxImages = maxImages,
                                    onRemoveImage = { imageEntity ->
                                        imageGalleryViewModel.removeImageFromToDo(imageEntity)
                                    },
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
                                                content = displayContent,
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
                                        title = {
                                            Text(
                                                stringResource(R.string.notification_permission_message_permenantly_denied_Title),
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        },
                                        text = {
                                            Text(
                                                stringResource(R.string.notification_permission_message_permenantly_denied),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
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
                                                Text(
                                                    text = "Open Settings",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = { showSettingsDialog = false }) {
                                                Text(
                                                    text = "Cancel",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        }
                                    )
                                }


                                Spacer(modifier = Modifier.height(5.dp))

                                // Button row
                                ActionButtonsRow(
                                    isEditing = isEditing,
                                    isChecked = isChecked,
                                    images = images,
                                    maxImages = maxImages,
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

                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.notification_cleared),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onImageSelected = { bitmap ->
                                        // Append the new bitmap to the list
                                        imageGalleryViewModel.addImageToToDo(
                                            nonNullItem.id,
                                            bitmap
                                        )
                                    },
                                    onCopyContentClick = {
                                        val contentTtoCopy =
                                            toDoViewModel.buildNoteContentToCopy(nonNullItem)
                                        clipboardManager.setText(AnnotatedString(contentTtoCopy))

                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.content_copied),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    rowModifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
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