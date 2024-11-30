package com.codedbykay.purenotes.utils

import com.codedbykay.purenotes.db.todo.GroupWithTodos
import com.codedbykay.purenotes.db.todo.ToDo

private fun getStatus(done: Boolean): String {
    return if (done) "✓" else "○"
}

fun buildShareContent(
    groupWithTodos: GroupWithTodos,
    shareTitle: String,
    shareNoteTitle: String
): String {
    return buildString {
        appendLine("${shareTitle}: ${groupWithTodos.group.name}")
        appendLine("\n${shareNoteTitle}:")
        groupWithTodos.todos.forEach { todo ->
            val status = getStatus(todo.done)
            appendLine("$status ${todo.title}")
            // Add description if it's not empty
            if (!todo.content.isNullOrBlank()) {
                appendLine("   ${todo.content}")
            }
            appendLine("")
        }
    }
}

fun buildNoteShareContent(todo: ToDo): String {
    return buildString {
        val status = getStatus(todo.done)
        appendLine("$status ${todo.title}")
        appendLine("")
        appendLine("   ${todo.content}")
        appendLine("")
    }

}