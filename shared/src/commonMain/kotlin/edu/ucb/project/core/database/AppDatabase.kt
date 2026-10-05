package edu.ucb.project.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import edu.ucb.project.feature.weather.data.dao.WeatherDao
import edu.ucb.project.feature.weather.data.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDao(): WeatherDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor :
    RoomDatabaseConstructor<AppDatabase> {

    override fun initialize(): AppDatabase
}