package edu.ucb.project.core.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<AppDatabase> {

    val appContext = context.applicationContext

    val dbFile =
        appContext.getDatabasePath(
            "weather.db"
        )

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}