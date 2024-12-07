package com.codedbykay.purenotes.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ToDoImage): Long

    @Delete
    suspend fun deleteImage(image: ToDoImage)

    @Query("DELETE FROM ToDoImages WHERE todoId = :todoId")
    suspend fun deleteAllImageByToDoId(todoId: Int)

    @Query("SELECT * FROM ToDoImages WHERE toDoId = :toDoId")
    suspend fun getImagesByTodoId(toDoId: Int): List<ToDoImage>

    @Query("SELECT * FROM ToDoImages WHERE toDoId = :toDoId")
    fun getImagesForToDo(toDoId: Int): LiveData<List<ToDoImage>>
}