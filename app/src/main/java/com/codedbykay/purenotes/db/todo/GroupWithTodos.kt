package com.codedbykay.purenotes.db.todo

data class GroupWithTodos(
    val group: ToDoGroup,
    val todos: List<ToDo>
)
