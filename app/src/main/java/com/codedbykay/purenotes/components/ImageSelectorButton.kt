package com.codedbykay.purenotes.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.utils.customCircleBackground
import com.codedbykay.purenotes.utils.decodeBitmap
import kotlin.collections.forEach

@Composable
fun ImageSelectorButton(
    iconButtonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    currentImageCount: Int,
    maxImages: Int,
    onImageSelected: (Bitmap) -> Unit,
) {
    val context = LocalContext.current

    // Calculate remaining images
    val remainingImages = maxImages - currentImageCount

    // Determine if button should be enabled
    val isButtonEnabled = remainingImages > 0

    // Single Image Picker Launcher
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            decodeBitmap(context, it)?.let { bitmap ->
                onImageSelected(bitmap)
            }
        }
    }

    // Multiple Image Picker Launcher (only if remainingImages >= 2)
    val multipleImagePickerLauncher = if (remainingImages >= 2) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = remainingImages)
        ) { uris ->
            uris.forEach { uri ->
                decodeBitmap(context, uri)?.let { bitmap ->
                    onImageSelected(bitmap)
                }
            }
        }
    } else {
        null
    }

    IconButton(
        onClick = {
            if (isButtonEnabled) {
                if (remainingImages == 1) {
                    singleImagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else if (remainingImages >= 2) {
                    multipleImagePickerLauncher?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
        },
        enabled = isButtonEnabled,
        modifier = iconButtonModifier
            .customCircleBackground(
                color = if (isButtonEnabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            .padding(2.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = if (isButtonEnabled) "Add image" else "Image limit reached",
            modifier = iconModifier,
            tint = if (isButtonEnabled) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Dimmed tint when disabled
        )
    }
}