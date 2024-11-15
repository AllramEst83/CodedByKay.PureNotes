package com.codedbykay.purenotes.db.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "ToDo",
    foreignKeys = [
        ForeignKey(
            entity = ToDoGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String? = null,
    val createdAt: Date,
    val done: Boolean,
    @ColumnInfo(index = true)
    val groupId: Int,
    val notificationTime: Long? = null,
    val notificationRequestCode: Int? = null,
    val notificationAction: String? = null,
    val notificationDataUri: String? = null
)
