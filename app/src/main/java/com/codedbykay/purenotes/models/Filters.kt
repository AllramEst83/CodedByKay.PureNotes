package com.codedbykay.purenotes.models

/**
 * Represents the completion status of a to-do item.
 */
enum class DoneStatus {
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

enum class NotificationTimeFilter {
    ALL,
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

data class ToDoGroupFilter(
    val createdDateFilter: CreatedDateFilter = CreatedDateFilter.ALL
)

data class ToDoFilter(
    val doneStatus: DoneStatus = DoneStatus.ALL,
    val createdDateFilter: CreatedDateFilter = CreatedDateFilter.ALL,
    val notificationDateFilter: NotificationTimeFilter = NotificationTimeFilter.ALL
)