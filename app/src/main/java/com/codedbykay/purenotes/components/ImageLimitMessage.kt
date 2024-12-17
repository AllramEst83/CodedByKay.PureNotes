package com.codedbykay.purenotes.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.codedbykay.purenotes.R
@Composable
fun ImageLimitMessage(
    modifier: Modifier = Modifier,
    showImageLimitMessage: Boolean,
    maxImages: Int,
) {

    if (showImageLimitMessage) {
        Text(
            text = stringResource(
                R.string.image_limit_message,
                maxImages
            ),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = modifier
        )
    }
}
//