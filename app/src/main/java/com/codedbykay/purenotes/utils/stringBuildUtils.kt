package com.codedbykay.purenotes.utils

import com.codedbykay.purenotes.db.GroupWithTodos
import com.codedbykay.purenotes.db.ToDo

private fun getStatus(done: Boolean): String {
    return if (done) "✓" else "○"
}

fun buildShareContent(
    groupWithTodos: GroupWithTodos,
): String {
    return buildString {
        appendLine(groupWithTodos.group.name)
        appendLine("")
        groupWithTodos.todos.forEachIndexed { index, todo ->
            val status = getStatus(todo.done)
            appendLine("$status ${todo.title}")
            // Add description if it's not empty
            if (!todo.content.isNullOrBlank()) {
                todo.content.lines().forEach { line ->
                    appendLine("   $line") // Indent each line of content
                }
                appendLine("")
            }

            // Avoid adding an extra newline after the last item
            if (index == groupWithTodos.todos.lastIndex) {
                deleteAt(length - 1) // Remove last newline
            }
        }
    }
}

fun buildNoteShareContent(todo: ToDo): String {
    return buildString {
        val status = getStatus(todo.done)
        appendLine("$status ${todo.title}")
        if (!todo.content.isNullOrBlank()) {
            appendLine(todo.content.trim())
        }
    }.trimEnd()
}
