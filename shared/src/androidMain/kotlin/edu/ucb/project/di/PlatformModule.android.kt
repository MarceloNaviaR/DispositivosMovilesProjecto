package edu.ucb.project.di

import edu.ucb.project.core.database.AppDatabase
import edu.ucb.project.core.database.getDatabaseBuilder
import edu.ucb.project.core.database.getRoomDatabase
import edu.ucb.project.feature.notification.AndroidNotificationService
import edu.ucb.project.feature.notification.domain.NotificationService
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single<AppDatabase> {
        getRoomDatabase(
            getDatabaseBuilder(get())
        )
    }

    single<NotificationService> {
        AndroidNotificationService(get())
    }
}