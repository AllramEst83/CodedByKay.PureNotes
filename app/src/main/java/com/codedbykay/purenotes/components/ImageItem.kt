package com.codedbykay.purenotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.ToDoImage

@Composable
fun ImageItem(
    imageEntity: ToDoImage,
    onImageClick: () -> Unit,
    onRemove: (ToDoImage) -> Unit,
    isEditing: Boolean,
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .size(100.dp)
    ) {

        if (isLoading) {
            ToDoImageShimmerPlaceholder()
        }

        AsyncImage(
            model = imageEntity.imageUri,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { isLoading = false },
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(15.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    onImageClick()
                },
            error = painterResource(id = R.drawable.ic_error_placeholder),
        )

        // Show the remove button if in editing mode
        if (isEditing) {
            IconButton(
                onClick = { onRemove(imageEntity) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                    .zIndex(2.0f)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Remove Image",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
