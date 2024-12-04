package com.codedbykay.purenotes.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ImageLimitMessage(
    modifier: Modifier = Modifier,
    showImageLimitMessage: Boolean,
    maxImages: Int,
) {

    if (showImageLimitMessage) {
        Text(
            text = "You have reached the maximum of $maxImages images.",
            color = Color.Gray,
            modifier = modifier
        )
    }
}
