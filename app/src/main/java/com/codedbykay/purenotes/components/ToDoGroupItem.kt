package com.codedbykay.purenotes.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.utils.customCircleBackground
import com.codedbykay.purenotes.utils.formatToString

@Composable
fun ToDoGroupItem(
    group: ToDoGroup,
    isChecked: Boolean = false,
    onClick: () -> Unit,
    onUpdate: (ToDoGroup) -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = group.createdAt.formatToString()
    var isEditing by remember { mutableStateOf(false) }
    var name by remember(group.id) { mutableStateOf(group.name) }
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "ExpandCollapseRotation"
    )

    Card(
        modifier = modifier
            .clickable {
                if (!isEditing) {
                    onClick()
                }
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!isChecked) 8.dp else 0.dp
        ),
        shape = MaterialTheme.shapes.small,

        ) {
        Box {
            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DisplayDate(
                                modifier = Modifier
                                    .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                                    .padding(horizontal = 11.dp, vertical = 6.dp)
                                    .alpha(if (isChecked) 0.5f else 1f),
                                formattedDate = formattedDate,
                                isChecked = isChecked
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTitleField(
                            editModifier = Modifier
                                .fillMaxWidth()

                                .alpha(if (isChecked) 0.5f else 1f),
                            textModifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)

                                .alpha(if (isChecked) 0.5f else 1f),
                            title = name,
                            onTitleChange = { name = it },
                            isEditing = isEditing,
                            isChecked = false

                        )

                        if (expanded) {

                            // Button row
                            GroupActionButtonsRow(
                                isEditing = isEditing,
                                onDeleteClick = onDelete,
                                onEditClick = {
                                    if (isEditing) {
                                        val updatedItem = group.copy(
                                            name = name
                                        )
                                        onUpdate(updatedItem)
                                        isEditing = false
                                    } else {
                                        isEditing = true
                                    }
                                },
                                onShareClick = onShare,
                                rowModifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .alpha(if (isChecked) 0.5f else 1f)
                            )
                        }
                    }
                }
            }

            ExpandCollapseButton(
                expanded = expanded,
                onClick = { expanded = !expanded },
                rotation = rotation,
                iconModifier = Modifier.align(Alignment.TopEnd),
                boxModifier = Modifier
                    .customCircleBackground(MaterialTheme.colorScheme.onSurface)
                    .padding(4.dp)
                    .alpha(if (isChecked) 0.5f else 1f)
            )
        }
    }
}