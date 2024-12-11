package com.codedbykay.purenotes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.constants.ThemeConstants.ThemeMode
import com.codedbykay.purenotes.models.ThemeOption

@Composable
fun ThemeItem(
    currentTheme: ThemeMode,
    themeOption: ThemeOption,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onThemeSelected(themeOption.mode) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = currentTheme == themeOption.mode,
            onClick = { onThemeSelected(themeOption.mode) },
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            text = stringResource(id = themeOption.displayNameRes),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}