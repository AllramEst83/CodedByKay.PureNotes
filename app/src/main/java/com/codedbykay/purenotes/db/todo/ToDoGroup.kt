package com.codedbykay.purenotes.db.todo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ToDoGroup")
data class ToDoGroup(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // Primary key for the group
    var name: String, // Name of the group, e.g., "Work", "Personal", "Shopping List"
    var createdAt: Date
)
