package edu.ucb.project.di

import edu.ucb.project.core.database.AppDatabase
import edu.ucb.project.core.database.getDatabaseBuilder
import edu.ucb.project.core.database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single<AppDatabase> {
        getRoomDatabase(
            getDatabaseBuilder(get())
        )
    }
}