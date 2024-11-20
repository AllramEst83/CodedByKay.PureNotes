package com.codedbykay.purenotes.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.BuildConfig
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.DoneStatusFilter
import com.codedbykay.purenotes.models.NotificationTimeFilter
import com.codedbykay.purenotes.models.Quadruple
import com.codedbykay.purenotes.models.SortOrder
import com.codedbykay.purenotes.models.ToDoFilter
import com.codedbykay.purenotes.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.Instant

class ToDoViewModel(
    private val notificationHelper: NotificationHelper,
) : ViewModel() {

    init {
        if (BuildConfig.DEBUG) {
            //populateDummyData()
        }
    }

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()
    private val todoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()

    // LiveData for search query
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> = _searchQuery

    // Filtering and sorting

    // LiveData to hold the current sort order
    private val _sortOrder = MutableLiveData(SortOrder.CREATED_AT_DESCENDING)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    // LiveData to hold the current filter
    private val _filter = MutableLiveData(ToDoFilter())
    val filter: LiveData<ToDoFilter> = _filter

    // MutableLiveData for selected groupId to filter by group
    private val _selectedGroupId = MutableLiveData<Int?>()

    // Combined LiveData that responds to changes in sort order, filter, and groupId
    private val combinedSearchFilterSort: LiveData<Quadruple<Int?, SortOrder, ToDoFilter, String?>> =
        MediatorLiveData<Quadruple<Int?, SortOrder, ToDoFilter, String?>>().apply {
            fun updateValue() {
                value = Quadruple(
                    _selectedGroupId.value,
                    _sortOrder.value ?: SortOrder.CREATED_AT_DESCENDING,
                    _filter.value ?: ToDoFilter(),
                    _searchQuery.value
                )
            }

            addSource(_sortOrder) { updateValue() }
            addSource(_filter) { updateValue() }
            addSource(_selectedGroupId) { updateValue() }
            addSource(_searchQuery) { updateValue() }
        }

    // Updated toDoList with dynamic search functionality
    val toDoList: LiveData<List<ToDo>> =
        combinedSearchFilterSort.switchMap { (groupId, sortOrder, filter, query) ->
            toDoDao.getFilteredToDo(
                doneStatus = when (filter.doneStatus) {
                    DoneStatusFilter.ALL -> "ALL"
                    DoneStatusFilter.DONE -> "DONE"
                    DoneStatusFilter.NOT_DONE -> "NOT_DONE"
                },
                createdDateFilter = when (filter.createdDateFilter) {
                    CreatedDateFilter.ALL -> "ALL"
                    CreatedDateFilter.TODAY -> "TODAY"
                    CreatedDateFilter.THIS_WEEK -> "THIS_WEEK"
                    CreatedDateFilter.THIS_MONTH -> "THIS_MONTH"
                },
                notificationDateFilter = when (filter.notificationDateFilter) {
                    NotificationTimeFilter.ALL -> "ALL"
                    NotificationTimeFilter.TODAY -> "TODAY"
                    NotificationTimeFilter.THIS_WEEK -> "THIS_WEEK"
                    NotificationTimeFilter.THIS_MONTH -> "THIS_MONTH"
                },
                sortOrder = sortOrder.name,
                groupId = groupId
            ).map { todoList ->
                // Apply search filtering only if the query is not null or empty
                if (query.isNullOrBlank()) {
                    todoList
                } else {
                    todoList.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.content?.contains(query, ignoreCase = true) == true
                    }
                }
            }
        }


    // Set specific group ID
    fun setGroupId(groupId: Int?) {
        _selectedGroupId.value = groupId
    }

    // Function to set a specific sort order
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    // Function to set filter
    fun setFilter(filter: ToDoFilter) {
        _filter.value = filter
    }

    // Setters for search query
    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    // Filtering and sorting

    // Schedule notification based on the current title, description, and time
    private fun scheduleNotification(
        id: Int,
        title: String,
        groupId: Int,
        description: String,
        time: Long
    ) {

        // Use NotificationHelper to schedule the notification
        notificationHelper.scheduleNotification(
            id = id,
            title = title,
            groupId = groupId,
            description = description,
            timeInMillis = time
        )
    }

    // CRUD
    fun addToDo(title: String, groupId: Int, content: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            toDoDao.addTodo(
                ToDo(
                    title = title,
                    content = content,
                    createdAt = Date.from(Instant.now()),
                    done = false,
                    groupId = groupId
                )
            )
        }
    }

    fun deleteToDo(
        id: Int,
        notificationRequestCode: Int?,
        notificationAction: String?,
        notificationDataUri: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationHelper
                .cancelNotification(
                    id,
                    notificationRequestCode,
                    notificationAction,
                    notificationDataUri
                )

            toDoDao.deleteTodo(id)
        }
    }

    fun deleteAllDoneToDos(groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoDao.deleteAllDoneToDosByGroupId(groupId)
        }
    }

    fun updateToDoAfterEdit(todo: ToDo) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoDao.updateToDoAfterEdit(todo.id, todo.title, todo.content)
        }
    }

    fun updateToDoDone(id: Int, done: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoDao.updateToDoDone(id, done)
        }
    }

    fun addToDoNotification(
        notificationTime: Long?,
        id: Int,
        title: String,
        content: String?,
        groupId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Schedule the notification
            scheduleNotification(
                id = id,
                title = title,
                groupId = groupId,
                description = if (content.isNullOrEmpty()) "Boom! Just do it!" else content,
                time = notificationTime ?: System.currentTimeMillis()
            )
        }
    }

    fun removeAlarmFromToDo(
        id: Int,
        notificationRequestCode: Int?,
        notificationAction: String?,
        notificationDataUri: String?
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            notificationHelper
                .cancelNotification(
                    id,
                    notificationRequestCode,
                    notificationAction,
                    notificationDataUri
                )
        }
    }
    // CRUD

    // Test data
    fun populateDummyData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTime = Instant.now()

            // Define some groups
            val groups = listOf(
                ToDoGroup(
                    name = "Work",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 30))
                ),
                ToDoGroup(
                    name = "Personal",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 7))
                ),
                ToDoGroup(
                    name = "Shopping List",
                    createdAt = Date.from(currentTime.minusSeconds(86400))
                )
            )

            // Clear existing data for a clean test environment
            todoGroupDao.deleteAllGroups()
            toDoDao.deleteAllToDos()

            // Insert groups and get their generated IDs
            val groupIds = groups.map { todoGroupDao.insertGroup(it).toInt() }

            // Define some todos with assigned group IDs
            val todos = listOf(
                ToDo(
                    title = "Buy groceries",
                    content = "Buy milk, eggs, bread",
                    createdAt = Date.from(currentTime.minusSeconds(0)),
                    done = false,
                    groupId = groupIds[2]
                ),
                ToDo(
                    title = "Morning exercise",
                    content = "Run 5km",
                    createdAt = Date.from(currentTime.minusSeconds(86400 + 60)),
                    done = true,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Read book",
                    content = "Read 'Clean Code'",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 7 + 120)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Finish project",
                    content = "Complete the UI for project",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 30 + 180)),
                    done = true,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Prepare presentation",
                    content = "Slides for team meeting",
                    createdAt = Date.from(currentTime.minusSeconds(3600 + 240)),
                    done = true,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Organize files",
                    content = "Clean up desktop and folders",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 2 + 300)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Plan vacation",
                    content = "Research destinations",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 14 + 360)),
                    done = true,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Schedule dentist appointment",
                    content = "Book appointment for next week",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 3 + 420)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Write blog post",
                    content = "Topic on Android development",
                    createdAt = Date.from(currentTime.minusSeconds(3600 * 5 + 480)),
                    done = false,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Check emails",
                    content = "Respond to important emails",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 5 + 540)),
                    done = false,
                    groupId = groupIds[1]
                )
            )

            // Insert todos with group associations and log each insertion
            todos.forEach { todo ->
                toDoDao.addTodo(todo)
            }

            // Define notification times (1 min, 5 min, 10 min from now)
            val oneMinuteFromNow = System.currentTimeMillis() + 1 * 60 * 1000
            val fiveMinutesFromNow = System.currentTimeMillis() + 5 * 60 * 1000
            val tenMinutesFromNow = System.currentTimeMillis() + 10 * 60 * 1000
            val notificationTimes = listOf(oneMinuteFromNow, fiveMinutesFromNow, tenMinutesFromNow)

            val allTodos = toDoDao.getAllToDos()

            // Schedule notifications for three ToDo items
            allTodos.take(3).forEachIndexed { index, todo ->
                if (!todo.done) {
                    val todoId = todo.id
                    val title = todo.title
                    val content = todo.content ?: ""
                    val groupId = groups[index].id

                    // Schedule the notification
                    scheduleNotification(
                        id = todoId,
                        title = title,
                        groupId = groupId,
                        description = "Reminder: $content",
                        time = notificationTimes[index]
                    )

                    Log.d(
                        "ToDoUpdate",
                        "Scheduled notification for ToDo ID: $todoId at ${notificationTimes[index]}"
                    )
                }
            }
        }
    }
// TEST data
}
