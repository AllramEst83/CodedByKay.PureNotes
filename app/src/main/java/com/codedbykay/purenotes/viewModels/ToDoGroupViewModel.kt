package com.codedbykay.purenotes.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.models.CreatedDateFilter
import com.codedbykay.purenotes.models.SortOrder
import com.codedbykay.purenotes.models.ToDoGroupFilter
import com.codedbykay.purenotes.services.ShareService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class ToDoGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val shareService = ShareService(application)

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()

    // Share data
    private val _shareContent = MutableLiveData<String?>()
    val shareContent: LiveData<String?> get() = _shareContent

    // LiveData for search query
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> = _searchQuery

    // LiveData to hold the current sort order
    private val _sortGroupOrder = MutableLiveData(SortOrder.CREATED_AT_DESCENDING)
    val sortOrder: LiveData<SortOrder> = _sortGroupOrder

    // LiveData to hold the current filter
    private val _groupFilter = MutableLiveData(ToDoGroupFilter())
    val groupFilter: LiveData<ToDoGroupFilter> = _groupFilter

    // Combined LiveData that responds to changes in sort order and filter
    private val combinedSortFilter: LiveData<Triple<SortOrder, ToDoGroupFilter, String>> =
        MediatorLiveData<Triple<SortOrder, ToDoGroupFilter, String>>().apply {
            fun updateValue() {
                value = Triple(
                    _sortGroupOrder.value ?: SortOrder.CREATED_AT_DESCENDING,
                    _groupFilter.value ?: ToDoGroupFilter(),
                    _searchQuery.value ?: ""
                )
            }

            addSource(_sortGroupOrder) { updateValue() }
            addSource(_groupFilter) { updateValue() }
            addSource(_searchQuery) { updateValue() }
        }

    // LiveData that triggers `getFilteredToDoGroups` when sort or filter changes
    val toDoGroupList: LiveData<List<ToDoGroup>> =
        combinedSortFilter.switchMap { (sortOrder, filter, query) ->
            toDoGroupDao.getFilteredToDoGroups(
                createdDateFilter = when (filter.createdDateFilter) {
                    CreatedDateFilter.ALL -> "ALL"
                    CreatedDateFilter.TODAY -> "TODAY"
                    CreatedDateFilter.THIS_WEEK -> "THIS_WEEK"
                    CreatedDateFilter.THIS_MONTH -> "THIS_MONTH"
                },
                sortOrder = sortOrder.name // Pass the sort order dynamically
            ).map { todoListGroup ->
                // Apply search filtering only if the query is not null or empty
                if (query.isNullOrBlank()) {
                    todoListGroup
                } else {
                    todoListGroup.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
            }
        }

    // Setters for search query
    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
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
            toDoGroupDao.insertGroup(newGroup)
        }
    }

    // Function to delete a group by its ID
    fun deleteGroupById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.deleteGroupById(id)
        }
    }

    fun updateGroup(id: Int, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoGroupDao.updateGroup(id = id, name = name)
        }
    }

    fun shareGroupAndToDos(groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val content = shareService.getShareContent(groupId)
            _shareContent.postValue(content)
        }
    }

    fun resetShareContent() {
        _shareContent.postValue(null)
    }


}