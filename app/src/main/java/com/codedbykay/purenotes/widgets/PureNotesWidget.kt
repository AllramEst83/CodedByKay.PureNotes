package com.codedbykay.purenotes.widgets

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.codedbykay.purenotes.MainActivity
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.ui.theme.widget.widgetDarkSchemeDefaultDark
import com.codedbykay.purenotes.ui.theme.widget.widgetLightSchemeDefaultLight
import com.codedbykay.purenotes.utils.isBiggerOrEqualTo
import com.codedbykay.purenotes.widgets.actions.DropdownItemClickAction
import com.codedbykay.purenotes.widgets.actions.ExpandDropdownAction
import com.codedbykay.purenotes.widgets.actions.MarkToDoAsDone
import com.codedbykay.purenotes.widgets.actions.RefreshNoteListsAction
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PureNotesWidget : GlanceAppWidget() {


    override val sizeMode = SizeMode.Responsive(
        // Responsive widget size
        setOf(
            DpSize(100.dp, 146.dp),   // Small vertical rectangle: 2x3
            DpSize(225.dp, 146.dp),   // Large vertical rectangle: 3x3
        )
    )

    companion object {
        //Screen state keys
        private val BIG_VERTICAL_SQUARE = DpSize(225.dp, 146.dp)

        //Keys for preferences
        val groupJsonKey = stringPreferencesKey("groupJsonKey")
        val selectedIndexKey = intPreferencesKey("selectedIndexKey")
        val isDropDownExpandedKey = booleanPreferencesKey("isDropDownExpanded")
        val selectedGroupIdKey = intPreferencesKey("selectedGroupId")
        val todoJsonKey = stringPreferencesKey("todoJson")
        val isAppInitializedKey = booleanPreferencesKey("is_app_initialized")

    }

    object PureNotesWidgetGlanceColorScheme {

        val colors = ColorProviders(
            light = widgetLightSchemeDefaultLight,
            dark = widgetDarkSchemeDefaultDark
        )
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme(colors = PureNotesWidgetGlanceColorScheme.colors) {
                val size = LocalSize.current
                Content(size)
            }
        }
    }


    @Composable
    private fun Content(size: DpSize) {
        val prefs = currentState<Preferences>()
        val groupJson = prefs[groupJsonKey] ?: "[]"
        val todosJson = prefs[todoJsonKey] ?: "[]"
        val isDropDownExpanded = prefs[isDropDownExpandedKey] == true
        val selectedIndex = prefs[selectedIndexKey] ?: 0
        val selectedGroupIdKey = prefs[selectedGroupIdKey] ?: -1
        val isAppInitialized = prefs[isAppInitializedKey] == true

        // Deserialize the JSON to a list of groups
        val gson = Gson()
        val groupListType = object : TypeToken<List<ToDoGroup>>() {}.type
        val groups: List<ToDoGroup> = if (groupJson.isNotEmpty()) {
            gson.fromJson(groupJson, groupListType)
        } else {
            emptyList()
        }

        // Verify selectedIndex is valid for current groups list
        val validSelectedIndex = selectedIndex.takeIf {
            it >= 0 && it < groups.size
        } ?: 0

        val todoListType = object : TypeToken<List<ToDo>>() {}.type
        val todos: List<ToDo> = gson.fromJson(todosJson, todoListType)

        if (isAppInitialized) {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(GlanceTheme.colors.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Box tightly wrapping around the text
                    Box(
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.background)
                            .cornerRadius(15.dp)
                            .padding(4.dp)
                            .clickable(onClick = actionStartActivity<MainActivity>()),
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_home),
                            contentDescription = "Pure Notes Icon",
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                            modifier = GlanceModifier
                                .size(30.dp)
                                .padding(4.dp)
                        )
                    }
                    Spacer(modifier = GlanceModifier.defaultWeight())

                    if (size.isBiggerOrEqualTo(BIG_VERTICAL_SQUARE)) {
                        Box(
                            modifier = GlanceModifier
                                .background(GlanceTheme.colors.background)
                                .cornerRadius(15.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Pure notes mini",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = GlanceTheme.colors.onBackground
                                )
                            )
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())
                    }

                    Box(
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.background)
                            .cornerRadius(15.dp)
                            .padding(4.dp)
                            .clickable(
                                onClick = actionRunCallback<RefreshNoteListsAction>(
                                    actionParametersOf(
                                        RefreshNoteListsAction.groupIdKey to selectedGroupIdKey,
                                    )
                                )
                            ),
                    ) {

                        Image(
                            provider = ImageProvider(R.drawable.ic_cached), // Replace with your icon drawable
                            contentDescription = "Pure Notes Icon",
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                            modifier = GlanceModifier
                                .size(30.dp)
                                .padding(4.dp) // Optional: Make the icon clickable
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                GroupDropdownMenu(
                    groups = groups,
                    isDropDownExpanded = isDropDownExpanded,
                    validSelectedIndex = validSelectedIndex
                )

                // List of Notes
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ListOfNotes(todos)
                }

            }
        } else {
            WidgetProgress(true)
        }
    }

    @Composable
    fun WidgetProgress(circular: Boolean, progress: Float = 0.0f) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (circular) {
                CircularProgressIndicator(
                    modifier = GlanceModifier.size(100.dp)
                )
            } else {
                LinearProgressIndicator(
                    progress = progress, // 50% progress
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    }

    @Composable
    fun WidgetItem(
        item: ToDo
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.background)
                .cornerRadius(15.dp)
                .padding(10.dp)
                .clickable(
                    actionRunCallback<MarkToDoAsDone>(
                        actionParametersOf(
                            MarkToDoAsDone.todoIdkey to item.id,
                            MarkToDoAsDone.groupIdkey to item.groupId
                        )
                    )
                )
        ) {

            Row(
                modifier = GlanceModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_radio_button),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                    contentDescription = "Description of the icon",
                    modifier = GlanceModifier
                        .size(18.dp)
                )

                Text(
                    modifier = GlanceModifier.padding(start = 8.dp),
                    text = item.title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = GlanceTheme.colors.onBackground
                    )
                )
            }
        }
    }

    @Composable
    fun ListOfNotes(
        listOfTodos: List<ToDo>
    ) {
        val emptyListText = LocalContext.current.getString(R.string.empty_list_message)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
        ) {
            if (listOfTodos.isNotEmpty()) {
                LazyColumn(
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    itemsIndexed(listOfTodos) { _, item ->
                        Row(
                            modifier = GlanceModifier
                                .padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            WidgetItem(item)
                        }
                    }
                }
            } else {
                // Handle empty list case
                Box(
                    modifier = GlanceModifier
                        .defaultWeight() // This helps with vertical space distribution
                        .fillMaxWidth()
                        .background(GlanceTheme.colors.background)
                        .cornerRadius(15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = emptyListText,
                        style = TextStyle(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = GlanceTheme.colors.onBackground
                        )
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GroupDropdownMenu(
        modifier: GlanceModifier = GlanceModifier,
        groups: List<ToDoGroup>,
        isDropDownExpanded: Boolean,
        validSelectedIndex: Int
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selected item with dropdown toggle
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(GlanceTheme.colors.background)
                    .cornerRadius(15.dp)
                    .clickable(onClick = actionRunCallback<ExpandDropdownAction>()),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.padding(8.dp)
                ) {
                    Text(
                        text = groups[validSelectedIndex].name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = GlanceTheme.colors.onBackground
                        )
                    )
                    if (groups.isNotEmpty()) {
                        Text(
                            text = " ▼",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = GlanceTheme.colors.onBackground
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(5.dp))

            // Dropdown menu
            if (isDropDownExpanded && groups.isNotEmpty()) {
                LazyColumn(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(GlanceTheme.colors.background)
                        .cornerRadius(15.dp)
                        .padding(vertical = 8.dp)
                ) {
                    itemsIndexed(groups) { index, group ->
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable(
                                    onClick = actionRunCallback<DropdownItemClickAction>(
                                        actionParametersOf(
                                            DropdownItemClickAction.indexKey to index,
                                            DropdownItemClickAction.groupIdKey to group.id
                                        )
                                    )
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = group.name,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = GlanceTheme.colors.onBackground
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}