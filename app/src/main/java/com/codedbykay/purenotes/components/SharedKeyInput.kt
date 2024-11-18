package com.codedbykay.purenotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.R

@Composable
fun SharedKeyInput(
    onSharedKeyChange: (String) -> Unit,
    onConnectionToggle: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    sharedKey: String,
    isTopicCreated: Boolean
) {
    var isSharedKeyEnabled by remember { mutableStateOf(!isTopicCreated) }
    var sharedKeyState by remember { mutableStateOf(sharedKey) }

    // Synchronize sharedKeyState with the sharedKey prop
    LaunchedEffect(sharedKey) {
        sharedKeyState = sharedKey
        isSharedKeyEnabled = isTopicCreated || sharedKey.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row for the switch and label
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.enable_shared_key),
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = isSharedKeyEnabled,
                onCheckedChange = { isChecked ->
                    if (!isTopicCreated) { // Only toggle if no topic exists
                        isSharedKeyEnabled = isChecked
                        onConnectionToggle(isChecked)
                        if (!isChecked) {
                            sharedKeyState = "" // Reset the field
                            onSharedKeyChange("")
                        }
                    }
                },
                enabled = !isTopicCreated // Disable switch only if a topic exists
            )
        }

        // Input and button visible only if the switch is enabled
        if (isSharedKeyEnabled) {
            OutlinedTextField(
                value = sharedKeyState,
                onValueChange = { newValue ->
                    sharedKeyState = newValue
                    onSharedKeyChange(newValue)
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.shared_key_input),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                enabled = !isTopicCreated, // Disable if a topic exists
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isTopicCreated) {
                        onDeleteClick() // Delete topic and subscription
                    } else {
                        onSaveClick() // Save and create topic
                    }
                },
                enabled = sharedKeyState.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isTopicCreated) {
                        stringResource(id = R.string.delete_key)
                    } else {
                        stringResource(id = R.string.save_key)
                    }
                )
            }
        }
    }
}
