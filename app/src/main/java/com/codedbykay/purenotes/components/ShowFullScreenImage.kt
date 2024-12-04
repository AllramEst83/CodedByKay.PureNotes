package com.codedbykay.purenotes.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

    selectedImage?.let { imageEntity ->
        Dialog(
            onDismissRequest = {
                scale = 1f
                offset = Offset(0f, 0f)
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Main Image
                AsyncImage(
                    model = imageEntity.imageUri,
                    contentDescription = "Fullscreen image",
                    contentScale = ContentScale.Fit,
                    onSuccess = { success ->
                        // Retrieve the Drawable from the ImageResult
                        val drawable = success.result.image
                        // Get intrinsic width and height
                        val width = drawable.width
                        val height = drawable.height
                        // Update the aspect ratio if height is non-zero to avoid division by zero
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
                                scale *= zoom
                                scale = scale.coerceIn(0.5f, 3f)
                                offset = if (scale == 1f) {
                                    Offset(0f, 0f)
                                } else {
                                    offset + pan
                                }
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                )


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

                    // Close Button with improved visibility
                    IconButton(
                        onClick = {
                            scale = 1f
                            offset = Offset(0f, 0f)
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