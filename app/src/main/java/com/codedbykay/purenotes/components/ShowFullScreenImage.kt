package com.codedbykay.purenotes.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.codedbykay.purenotes.db.ToDoImage
import com.codedbykay.purenotes.utils.customCircleBackground

@Composable
fun ShowFullScreenImage(
    selectedImage: ToDoImage?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f

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
            // Use BoxWithConstraints to get the container dimensions
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                val boxWithConstraintsScope = this
                val containerWidth = boxWithConstraintsScope.constraints.maxWidth.toFloat()
                val containerHeight = boxWithConstraintsScope.constraints.maxHeight.toFloat()

                // Initialize image dimensions to container size
                var imageWidth by remember { mutableFloatStateOf(containerWidth) }
                var imageHeight by remember { mutableFloatStateOf(containerHeight) }

                // Load the image
                AsyncImage(
                    model = imageEntity.imageUri,
                    contentDescription = "Fullscreen image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
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

                                // Calculate the max offset based on scaled image and container sizes
                                val maxX = ((imageWidth * scale) - containerWidth) / 2
                                val maxY = ((imageHeight * scale) - containerHeight) / 2

                                // Constrain the offset to prevent over-panning
                                val newOffsetX = if (maxX > 0f) {
                                    offset.x.coerceIn(-maxX, maxX)
                                } else {
                                    // Center the image horizontally
                                    0f
                                }

                                val newOffsetY = if (maxY > 0f) {
                                    offset.y.coerceIn(-maxY, maxY)
                                } else {
                                    // Center the image vertically
                                    0f
                                }

                                offset = Offset(newOffsetX, newOffsetY)
                            }
                        }

                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    onSuccess = { success ->
                        val drawable = success.result.image
                        val intrinsicWidth = drawable.width.toFloat()
                        val intrinsicHeight = drawable.height.toFloat()

                        if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                            // Update image dimensions
                            val imageAspectRatio = intrinsicWidth / intrinsicHeight
                            val containerAspectRatio = containerWidth / containerHeight

                            if (imageAspectRatio >= containerAspectRatio) {
                                imageWidth = containerWidth
                                imageHeight = containerWidth / imageAspectRatio
                            } else {
                                imageHeight = containerHeight
                                imageWidth = containerHeight * imageAspectRatio
                            }
                        } else {
                            // Handle the case where dimensions are zero
                            imageWidth = containerWidth
                            imageHeight = containerHeight
                        }
                    },
                    onError = { error ->
                        // Handle the error, possibly show a placeholder or dismiss the dialog
                        Log.d("AsyncImage", "Image loading failed", error.result.throwable)
                        onDismiss()
                    }

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
