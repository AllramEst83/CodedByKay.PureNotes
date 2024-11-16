package com.codedbykay.purenotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.utils.blendWithWhite
import com.codedbykay.purenotes.utils.contrastColor

@Composable
fun DrawerContent(onNavigateToSettings: () -> Unit, onCloseDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.blendWithWhite(0.3f, 0.1f),
                        MaterialTheme.colorScheme.primary.blendWithWhite(0.3f, 0.1f),
                        MaterialTheme.colorScheme.secondary.blendWithWhite(0.3f, 0.1f),
                        MaterialTheme.colorScheme.secondary.blendWithWhite(0.3f, 0.1f),
                        MaterialTheme.colorScheme.tertiary.blendWithWhite(0.3f, 0.1f),
                        MaterialTheme.colorScheme.tertiary.blendWithWhite(0.3f, 0.1f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.menu),
                    color = MaterialTheme.colorScheme.primary.contrastColor(),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                IconButton(onClick = onCloseDrawer) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = MaterialTheme.colorScheme.primary.contrastColor()
                    )
                }
            }
            HorizontalDivider() // Divider in the drawer content
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation item for Theme Settings
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.settings)) },
                selected = false,
                onClick = onNavigateToSettings
            )
            // Add more navigation items here if needed
        }
    }
}