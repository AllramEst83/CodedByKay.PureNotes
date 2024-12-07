package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.db.ToDoImage


@Composable
fun ImageGallery(
    images: List<ToDoImage>,
    maxImages: Int,
    onRemoveImage: (ToDoImage) -> Unit,
    isEditing: Boolean,
    isChecked: Boolean,
) {
    var selectedImage by remember { mutableStateOf<ToDoImage?>(null) }
    val showImageLimit = images.size == maxImages

    Spacer(modifier = Modifier.height(8.dp))

    ImageLimitMessage(
        Modifier
            .fillMaxWidth()
            .padding(
                vertical = 7.dp
            ),
        showImageLimit,
        maxImages
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isChecked) 0.5f else 1f),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(images) { index, imageEntity ->
            Box(
                modifier = Modifier.size(100.dp) // Perhaps use dynamical size based on device resolution
            ) {
                ImageItem(
                    imageEntity = imageEntity,
                    onImageClick = { selectedImage = imageEntity },
                    onRemove = { onRemoveImage(imageEntity) },
                    isEditing = isEditing
                )
            }
        }
    }

    // Fullscreen image dialog
    ShowFullScreenImage(
        selectedImage,
        onDismiss = {
            selectedImage = null
        },
    )
}
