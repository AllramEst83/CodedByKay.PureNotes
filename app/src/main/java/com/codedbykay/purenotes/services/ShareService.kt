package com.codedbykay.purenotes.services

import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.GroupWithTodos
import com.codedbykay.purenotes.utils.buildShareContent

class ShareService {

    private val toDoGroupDao = MainApplication.toDoDatabase.getTodoGroupDao()
    private val toDoDao = MainApplication.toDoDatabase.getTodoDao()

    suspend fun getShareContent(groupId: Int): String? {
        val todos = toDoDao.getToDosToShareByGroupId(groupId)
        val group = toDoGroupDao.getGroupById(groupId)
        return if (group != null) {
            val groupWithTodos = GroupWithTodos(group, todos)

            buildShareContent(
                groupWithTodos
            )
        } else {
            null
        }
    }
}
