package com.codedbykay.purenotes

import android.app.Application
import com.codedbykay.purenotes.db.todo.ToDoDatabase
import com.codedbykay.purenotes.managers.LocaleManager
import com.codedbykay.purenotes.managers.ThemeDataStoreManager
import com.codedbykay.purenotes.services.ImageStorageService
import com.codedbykay.purenotes.services.IntentService
import com.codedbykay.purenotes.services.NotificationService

class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
            private set

        // Database
        val toDoDatabase: ToDoDatabase by lazy {
            ToDoDatabase.getInstance(instance)
        }

        // Managers
        val imageStorageService: ImageStorageService by lazy {
            ImageStorageService(instance.applicationContext)
        }

        val notificationService: NotificationService by lazy {
            NotificationService(instance.applicationContext)
        }

        // Services
        val localeManager: LocaleManager by lazy {
            LocaleManager(instance.applicationContext)
        }

        val themeDataStoreManager: ThemeDataStoreManager by lazy {
            ThemeDataStoreManager(instance.applicationContext)
        }

        val IntentService: IntentService by lazy {
            IntentService(instance.applicationContext)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
