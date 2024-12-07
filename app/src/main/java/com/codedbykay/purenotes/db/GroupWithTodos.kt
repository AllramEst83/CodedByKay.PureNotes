package com.codedbykay.purenotes.db


data class GroupWithTodos(
    val group: ToDoGroup,
    val todos: List<ToDo>,
)
