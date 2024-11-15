package com.codedbykay.purenotes

import android.app.Application
import com.codedbykay.purenotes.db.todo.ToDoDatabase

class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
            private set

        val toDoDatabase: ToDoDatabase by lazy {
            ToDoDatabase.getInstance(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
