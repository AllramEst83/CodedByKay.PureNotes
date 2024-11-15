package com.codedbykay.purenotes.db.todo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ToDoDao {
    // Sorting and filter queries
    @Query(
        """
    SELECT * FROM ToDo
    WHERE (:doneStatus = 'ALL' OR done = (:doneStatus = 'DONE'))
    AND (
        :createdDateFilter = 'ALL' OR
        (:createdDateFilter = 'TODAY' AND date(createdAt / 1000, 'unixepoch') = date('now')) OR
        (:createdDateFilter = 'THIS_WEEK' AND strftime('%W', createdAt / 1000, 'unixepoch') = strftime('%W', 'now')) OR
        (:createdDateFilter = 'THIS_MONTH' AND strftime('%m', createdAt / 1000, 'unixepoch') = strftime('%m', 'now'))
    )
    AND (:groupId IS NULL OR groupId = :groupId)
    ORDER BY 
        CASE 
            WHEN :sortOrder = 'CREATED_AT_ASCENDING' THEN createdAt 
        END ASC,
        CASE 
            WHEN :sortOrder = 'CREATED_AT_DESCENDING' THEN createdAt 
        END DESC,
        CASE 
            WHEN :sortOrder = 'TITLE_ASCENDING' THEN title 
        END ASC,
        CASE 
            WHEN :sortOrder = 'TITLE_DESCENDING' THEN title 
        END DESC
"""
    )
    fun getFilteredToDo(
        doneStatus: String,
        createdDateFilter: String,
        sortOrder: String,
        groupId: Int?
    ): LiveData<List<ToDo>>


    // CRUD Operations
    // Get all todos
    @Query("SELECT * FROM ToDo")
    fun getAllToDos(): List<ToDo>

    // Add todo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todo: ToDo): Long

    // Delete todo by id
    @Query("DELETE FROM ToDo WHERE id = :id")
    suspend fun deleteTodo(id: Int)

    // Delete all todos
    @Query("DELETE FROM ToDo")
    suspend fun deleteAllToDos()

    // Get todo by id
    @Query("SELECT * FROM ToDo WHERE id = :id")
    suspend fun getToDoById(id: Int): ToDo

    // Get todo by id (LiveData)
    @Query("SELECT * FROM ToDo WHERE id = :id")
    fun getToDoByIdLive(id: Int): LiveData<ToDo>

    // Remove all done todos for a given group
    @Query("DELETE FROM ToDo WHERE groupId = :groupId and done = 1 ")
    suspend fun deleteAllDoneToDosByGroupId(groupId: Int)

    // Update todo done state
    @Query("UPDATE ToDo SET done = :done WHERE id = :id")
    fun updateToDoDone(id: Int, done: Boolean)

    // Update title, content of todo
    @Query("UPDATE ToDo SET title = :title, content = :content WHERE id = :id")
    fun updateToDoAfterEdit(id: Int, title: String, content: String?)

    // Get group name by id
    @Query("SELECT name FROM ToDoGroup WHERE id = :groupId")
    fun getGroupNameById(groupId: Int): String

    // Set Alarm for todo
    @Query("UPDATE ToDo SET notificationRequestCode = :notificationRequestCode, notificationAction = :notificationAction, notificationDataUri = :notificationDataUri, notificationTime = :notificationTime WHERE id = :id")
    fun setAlarmForToDo(
        id: Int,
        notificationRequestCode: Int?,
        notificationAction: String?,
        notificationDataUri: String?,
        notificationTime: Long?
    )

    // Remove alarm from todo
    @Query("UPDATE ToDo SET notificationRequestCode = null, notificationAction = null, notificationDataUri = null, notificationTime = null WHERE id = :id")
    suspend fun removeAlarmFromToDo(id: Int)
}
