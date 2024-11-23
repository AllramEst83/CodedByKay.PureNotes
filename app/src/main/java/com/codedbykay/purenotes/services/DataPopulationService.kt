package com.codedbykay.purenotes.services

import android.util.Log
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.db.todo.ToDoDao
import com.codedbykay.purenotes.db.todo.ToDoGroup
import com.codedbykay.purenotes.db.todo.ToDoGroupDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.Date

//To populate dummy data for manual testing
class DataPopulationService(
    private val todoGroupDao: ToDoGroupDao,
    private val toDoDao: ToDoDao,
    private val scheduleNotification: (id: Int, title: String, groupId: Int, description: String, time: Long) -> Unit
) {
    suspend fun populateDummyData() {
        withContext(Dispatchers.IO) {
            val currentTime = Instant.now()

            // Define some groups
            val groups = listOf(
                ToDoGroup(
                    name = "Work",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 30))
                ),
                ToDoGroup(
                    name = "Personal",
                    createdAt = Date.from(currentTime.minusSeconds(86400 * 7))
                ),
                ToDoGroup(
                    name = "Shopping List",
                    createdAt = Date.from(currentTime.minusSeconds(86400))
                )
            )

            // Clear existing data for a clean test environment
            todoGroupDao.deleteAllGroups()
            toDoDao.deleteAllToDos()

            // Insert groups and get their generated IDs
            val groupIds = groups.map { todoGroupDao.insertGroup(it).toInt() }

            // Define some todos with assigned group IDs
            val todos = listOf(
                ToDo(
                    title = "Buy groceries",
                    content = "Buy milk, eggs, bread",
                    createdAt = Date.from(currentTime),
                    done = false,
                    groupId = groupIds[2]
                ),
                ToDo(
                    title = "Morning exercise",
                    content = "Run 5km",
                    createdAt = Date.from(currentTime.minusSeconds(86460)),
                    done = true,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Read book",
                    content = "Read 'Clean Code'",
                    createdAt = Date.from(currentTime.minusSeconds(604920)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Finish project",
                    content = "Complete the UI for project",
                    createdAt = Date.from(currentTime.minusSeconds(2592180)),
                    done = true,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Prepare presentation",
                    content = "Slides for team meeting",
                    createdAt = Date.from(currentTime.minusSeconds(3600)),
                    done = true,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Organize files",
                    content = "Clean up desktop and folders",
                    createdAt = Date.from(currentTime.minusSeconds(172830)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Plan vacation",
                    content = "Research destinations",
                    createdAt = Date.from(currentTime.minusSeconds(1209600)),
                    done = true,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Schedule dentist appointment",
                    content = "Book appointment for next week",
                    createdAt = Date.from(currentTime.minusSeconds(259200)),
                    done = false,
                    groupId = groupIds[1]
                ),
                ToDo(
                    title = "Write blog post",
                    content = "Topic on Android development",
                    createdAt = Date.from(currentTime.minusSeconds(18000)),
                    done = false,
                    groupId = groupIds[0]
                ),
                ToDo(
                    title = "Check emails",
                    content = "Respond to important emails",
                    createdAt = Date.from(currentTime.minusSeconds(432000)),
                    done = false,
                    groupId = groupIds[1]
                )
            )

            // Insert todos with group associations
            todos.forEach { todo -> toDoDao.addTodo(todo) }

            // Define notification times
            val notificationTimes = listOf(
                System.currentTimeMillis() + 1 * 60 * 1000,
                System.currentTimeMillis() + 5 * 60 * 1000,
                System.currentTimeMillis() + 10 * 60 * 1000
            )

            val allTodos = toDoDao.getAllToDos()

            // Schedule notifications for three ToDo items
            allTodos.take(3).forEachIndexed { index, todo ->
                if (!todo.done) {
                    scheduleNotification(
                        todo.id,
                        todo.title,
                        groups[index].id,
                        "Reminder: ${todo.content ?: ""}",
                        notificationTimes[index]
                    )

                    Log.d(
                        "ToDoUpdate",
                        "Scheduled notification for ToDo ID: ${todo.id} at ${notificationTimes[index]}"
                    )
                }
            }
        }
    }
}