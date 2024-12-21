package com.codedbykay.purenotes.modules

import android.app.Activity
import com.codedbykay.purenotes.MainActivity
import com.codedbykay.purenotes.db.ToDoDatabase
import com.codedbykay.purenotes.managers.LocaleManager
import com.codedbykay.purenotes.managers.PermissionManager
import com.codedbykay.purenotes.managers.ThemeDataStoreManager
import com.codedbykay.purenotes.services.ImageStorageService
import com.codedbykay.purenotes.services.IntentService
import com.codedbykay.purenotes.services.NotificationService
import com.codedbykay.purenotes.services.ShareService
import com.codedbykay.purenotes.viewModels.ImageGalleryViewModel
import com.codedbykay.purenotes.viewModels.SettingsViewModel
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { ToDoDatabase.getInstance(get()) }

    // Factories
    factory { get<ToDoDatabase>().getTodoDao() }
    factory { get<ToDoDatabase>().getTodoGroupDao() }
    factory { get<ToDoDatabase>().getImageDao() }

    // Services
    single<ImageStorageService> { ImageStorageService(get()) }
    single<NotificationService> { NotificationService(get(), get()) }
    single<LocaleManager> { LocaleManager(get()) }
    single<ThemeDataStoreManager> { ThemeDataStoreManager(get()) }
    single<IntentService> { IntentService(get()) }
    single<ShareService> { ShareService(get(), get()) }
    scope<MainActivity> {
        scoped { (activity: Activity) -> PermissionManager(activity) }
    }

    // ViewModels
    viewModel { ToDoViewModel(get(), get()) }
    viewModel { ToDoGroupViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { ImageGalleryViewModel(get(), get(), get()) }
}