package com.codedbykay.purenotes.services

import com.codedbykay.purenotes.db.GroupWithTodos
import com.codedbykay.purenotes.db.ToDoDao
import com.codedbykay.purenotes.db.ToDoGroupDao
import com.codedbykay.purenotes.utils.buildShareContent
import org.koin.core.component.KoinComponent

class ShareService(
    private val toDoDao: ToDoDao,
    private val toDoGroupDao: ToDoGroupDao,
) : KoinComponent {

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
