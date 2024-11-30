package com.codedbykay.purenotes.utils

import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.GroupWithTodos
import com.codedbykay.purenotes.db.todo.ToDo

fun buildShareContent(
    groupWithTodos: GroupWithTodos,
    shareTitle: String,
    shareNoteTitle: String
): String {
    return buildString {
        appendLine("${shareTitle}: ${groupWithTodos.group.name}")
        appendLine("\n${shareNoteTitle}:")
        groupWithTodos.todos.forEach { todo ->
            val status = if (todo.done) "✓" else "○"
            appendLine("$status ${todo.title}")
            // Add description if it's not empty
            if (!todo.content.isNullOrBlank()) {
                appendLine("   ${todo.content}")
            }
            appendLine("")
        }
        appendLine("\n${R.string.share_shared_from}")
    }
}

fun buildNoteShareContent(todo: ToDo): String {
    return buildString {
        val status = if (todo.done) "✓" else "○"
        appendLine("$status ${todo.title}")
        appendLine("")
        appendLine("   ${todo.content}")
    }

}