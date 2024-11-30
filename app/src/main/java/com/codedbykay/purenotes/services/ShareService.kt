package com.codedbykay.purenotes.services

import android.content.Context
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.GroupWithTodos

class ShareService(private val context: Context) {

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    suspend fun getShareContent(groupId: Int): String? {
        val todos = toDoDao.getToDosByGroupId(groupId)
        val group = toDoGroupDao.getGroupById(groupId)
        return if (group != null) {
            val groupWithTodos = GroupWithTodos(group, todos)
            buildShareContent(groupWithTodos)
        } else {
            null
        }
    }

    private fun buildShareContent(groupWithTodos: GroupWithTodos): String {
        return buildString {
            appendLine("${context.getString(R.string.share_group_title)}: ${groupWithTodos.group.name}")
            appendLine("\n${context.getString(R.string.share_note_title)}:")
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
}
