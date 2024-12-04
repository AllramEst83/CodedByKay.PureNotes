package com.codedbykay.purenotes.db.todo


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ToDoGroupDao {

    @Query(
        """
    SELECT * FROM ToDoGroup
    WHERE 
        (:createdDateFilter = 'ALL' OR 
        (:createdDateFilter = 'TODAY' AND date(createdAt / 1000, 'unixepoch') = date('now')) OR
        (:createdDateFilter = 'THIS_WEEK' AND strftime('%W', createdAt / 1000, 'unixepoch') = strftime('%W', 'now')) OR
        (:createdDateFilter = 'THIS_MONTH' AND strftime('%m', createdAt / 1000, 'unixepoch') = strftime('%m', 'now')))
    ORDER BY 
        CASE 
            WHEN :sortOrder = 'CREATED_AT_ASCENDING' THEN createdAt 
        END ASC,
        CASE 
            WHEN :sortOrder = 'CREATED_AT_DESCENDING' THEN createdAt 
        END DESC,
        CASE 
            WHEN :sortOrder = 'TITLE_ASCENDING' THEN name 
        END ASC,
        CASE 
            WHEN :sortOrder = 'TITLE_DESCENDING' THEN name 
        END DESC
    """
    )
    fun getFilteredToDoGroups(
        createdDateFilter: String,
        sortOrder: String,
    ): LiveData<List<ToDoGroup>>


    // Insert a new group
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: ToDoGroup): Long

    // Get all groups
    @Query("SELECT * FROM ToDoGroup ORDER BY createdAt DESC")
    fun getAllGroups(): LiveData<List<ToDoGroup>>

    @Query("SELECT * FROM ToDoGroup ORDER BY createdAt DESC")
    fun getAllGroupsForWorker(): List<ToDoGroup>

    // Get a specific group by ID
    @Query("SELECT * FROM ToDoGroup WHERE id = :groupId")
    suspend fun getGroupById(groupId: Int): ToDoGroup?


    @Query("UPDATE ToDoGroup SET name = :name WHERE id = :id")
    fun updateGroup(id: Int, name: String)

    // Delete a specific group
    @Query("DELETE FROM ToDoGroup WHERE id = :id")
    suspend fun deleteGroupById(id: Int)

    // Delete all group
    @Query("DELETE FROM ToDoGroup")
    suspend fun deleteAllGroups()

}
