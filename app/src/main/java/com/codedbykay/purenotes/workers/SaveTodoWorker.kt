package com.codedbykay.purenotes.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDo
import java.util.Date

class SaveTodoWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val inputText = inputData.getString("todo_text") ?: return Result.failure()
        val groupId = inputData.getInt("group_id", -1)

        if (groupId == -1) return Result.failure()

        // Save the ToDo item in Room DB
        val toDoDao = MainApplication.toDoDatabase.getTodoDao()
        toDoDao.addTodo(
            ToDo(
                title = inputText,
                groupId = groupId,
                done = false,
                createdAt = Date()
            )
        )

        return Result.success()
    }
}
