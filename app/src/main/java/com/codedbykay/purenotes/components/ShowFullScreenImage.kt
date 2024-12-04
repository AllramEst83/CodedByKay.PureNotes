package com.codedbykay.purenotes.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.codedbykay.purenotes.db.todo.ToDoImage
import com.codedbykay.purenotes.utils.customCircleBackground


@Composable
fun ShowFullScreenImage(
    selectedImage: ToDoImage?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var aspectRatio by remember { mutableFloatStateOf(1f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 3f

    selectedImage?.let { imageEntity ->
        Dialog(
            onDismissRequest = {
                scale = 1f
                offset = Offset.Zero
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                // Main Image
                AsyncImage(
                    model = imageEntity.imageUri,
                    contentDescription = "Fullscreen image",
                    contentScale = ContentScale.None,
                    onSuccess = { success ->
                        val drawable = success.result.image
                        val width = drawable.width
                        val height = drawable.height
                        if (height > 0) {
                            aspectRatio = width.toFloat() / height.toFloat()
                        }
                    },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val previousScale = scale
                                scale = (scale * zoom).coerceIn(minScale, maxScale)
                                val scaleChange = scale / previousScale
                                offset += pan * scaleChange
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )

                // Share and Close Buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Share Button
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, imageEntity.imageUri)
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Image"))
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp)
                            .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_share),
                            contentDescription = "Share Image",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = {
                            scale = 1f
                            offset = Offset.Zero
                            onDismiss()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp)
                            .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Close Fullscreen",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
