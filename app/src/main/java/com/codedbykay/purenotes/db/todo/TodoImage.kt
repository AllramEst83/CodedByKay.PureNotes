package com.codedbykay.purenotes.db.todo


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ToDoImages",
    foreignKeys = [
        ForeignKey(
            entity = ToDo::class,
            parentColumns = ["id"],
            childColumns = ["toDoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["toDoId"])]
)
data class ToDoImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val toDoId: Int,  // Remove @ColumnInfo(index = true)
    val imageUri: String,
)