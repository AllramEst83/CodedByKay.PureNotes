package com.codedbykay.purenotes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.managers.PubSubManager
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.SortOrder
import com.codedbykay.purenotes.models.ToDoGroupFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class ToDoGroupViewModel : ViewModel() {
    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()

    // LiveData to hold the current sort order
    private val _sortGroupOrder = MutableLiveData(SortOrder.CREATED_AT_DESCENDING)
    val sortOrder: LiveData<SortOrder> = _sortGroupOrder

    // LiveData to hold the current filter
    private val _groupFilter = MutableLiveData(ToDoGroupFilter())
    val groupFilter: LiveData<ToDoGroupFilter> = _groupFilter

    // Combined LiveData that responds to changes in sort order and filter
    private val combinedSortFilter: LiveData<Pair<SortOrder, ToDoGroupFilter>> =
        MediatorLiveData<Pair<SortOrder, ToDoGroupFilter>>().apply {
            addSource(_sortGroupOrder) { sort ->
                value = Pair(sort, _groupFilter.value ?: ToDoGroupFilter())
            }
            addSource(_groupFilter) { filter ->
                value = Pair(_sortGroupOrder.value ?: SortOrder.CREATED_AT_DESCENDING, filter)
            }
        }

    // LiveData that triggers `getFilteredToDoGroups` when sort or filter changes
    val toDoGroupList: LiveData<List<ToDoGroup>> =
        combinedSortFilter.switchMap { (sortOrder, filter) ->
            toDoGroupDao.getFilteredToDoGroups(
                createdDateFilter = when (filter.createdDateFilter) {
                    CreatedDateFilter.ALL -> "ALL"
                    CreatedDateFilter.TODAY -> "TODAY"
                    CreatedDateFilter.THIS_WEEK -> "THIS_WEEK"
                    CreatedDateFilter.THIS_MONTH -> "THIS_MONTH"
                },
                sortOrder = sortOrder.name // Pass the sort order dynamically
            )
        }

    // Function to set a specific sort order
    fun setSortOrder(order: SortOrder) {
        _sortGroupOrder.value = order
    }

    // Function to set filter
    fun setFilter(groupFilter: ToDoGroupFilter) {
        _groupFilter.value = groupFilter
    }
    // Sorting and filtering

    // Function to add a new group
    fun addGroup(name: String, createdAt: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val newGroup = ToDoGroup(name = name, createdAt = createdAt)
            val groupId = toDoGroupDao.insertGroup(newGroup).toInt()

            // Publish message
            PubSubManager.publishMessage(
                action = "create",
                resourceType = "group",
                resourceId = groupId.toString(),
                content = mapOf(
                    "title" to name,
                    "createdAt" to createdAt.time.toString()
                )
            )
        }
    }


    fun addGroupFromSync(name: String, createdAt: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val newGroup = ToDoGroup(name = name, createdAt = createdAt)
            toDoGroupDao.insertGroup(newGroup)
        }
    }
    // Function to delete a group by its ID
    fun deleteGroupById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.deleteGroupById(id)

            // Publish message
            PubSubManager.publishMessage(
                action = "delete",
                resourceType = "group",
                resourceId = id.toString()
            )
        }
    }

    fun deleteGroupByIdFromSync(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.deleteGroupById(id)
        }
    }

    fun updateGroup(id: Int, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.updateGroup(id = id, name = name)

            // Publish message
            PubSubManager.publishMessage(
                action = "update",
                resourceType = "group",
                resourceId = id.toString(),
                content = mapOf("title" to name)
            )
        }
    }

    fun updateGroupFromSync(id: Int, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.updateGroup(id = id, name = name)
        }
    }
}