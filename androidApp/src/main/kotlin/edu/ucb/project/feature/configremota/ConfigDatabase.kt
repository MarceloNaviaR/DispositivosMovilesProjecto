package edu.ucb.project.feature.configremota

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ConfigVersionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ConfigDatabase : RoomDatabase() {

    abstract fun configDao(): ConfigVersionDao

    companion object {

        @Volatile
        private var INSTANCE: ConfigDatabase? = null

        fun getDatabase(context: Context): ConfigDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ConfigDatabase::class.java,
                    "config_remota_db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
