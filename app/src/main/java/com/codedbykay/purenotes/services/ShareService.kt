package com.codedbykay.purenotes.services

import android.content.Context
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.GroupWithTodos
import com.codedbykay.purenotes.utils.buildShareContent

class ShareService(private val context: Context) {

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    suspend fun getShareContent(groupId: Int): String? {
        val todos = toDoDao.getToDosByGroupId(groupId)
        val group = toDoGroupDao.getGroupById(groupId)
        return if (group != null) {
            val groupWithTodos = GroupWithTodos(group, todos)

            buildShareContent(
                groupWithTodos,
                context.getString(R.string.share_group_title),
                context.getString(R.string.share_note_title)
            )
        } else {
            null
        }
    }
}
