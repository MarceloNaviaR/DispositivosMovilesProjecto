package edu.ucb.project.core.database

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import edu.ucb.project.roomastruth.data.datasource.ConfigSeedCallback
import kotlinx.coroutines.Dispatchers

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {

    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(ConfigSeedCallback())
        // v1 -> v2 (nueva tabla config_item): recrea la BD. Aceptable en desarrollo;
        // el clima es solo caché. En producción habría que escribir una Migration.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}