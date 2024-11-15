package com.codedbykay.purenotes.models

enum class DoneStatusFilter {
    ALL,
    DONE,
    NOT_DONE
}

enum class CreatedDateFilter {
    ALL,
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

data class ToDoGroupFilter(
    val createdDateFilter: CreatedDateFilter = CreatedDateFilter.ALL
)

data class ToDoFilter(
    val doneStatus: DoneStatusFilter = DoneStatusFilter.ALL,
    val createdDateFilter: CreatedDateFilter = CreatedDateFilter.ALL
)