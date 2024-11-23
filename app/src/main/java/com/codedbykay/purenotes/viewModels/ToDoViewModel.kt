package com.codedbykay.purenotes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.DoneStatus
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

    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

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
                    DoneStatus.ALL -> "ALL"
                    DoneStatus.DONE -> "DONE"
                    DoneStatus.NOT_DONE -> "NOT_DONE"
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

    // CRUD
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
        content: String,
        groupId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Schedule the notification
            scheduleNotification(
                id = id,
                title = title,
                groupId = groupId,
                description = content,
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
}
