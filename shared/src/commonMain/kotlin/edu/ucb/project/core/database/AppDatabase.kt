package edu.ucb.project.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import edu.ucb.project.feature.weather.data.dao.WeatherDao
import edu.ucb.project.feature.weather.data.entity.WeatherEntity
import edu.ucb.project.roomastruth.data.dao.ConfigDao
import edu.ucb.project.roomastruth.data.entity.ConfigItemEntity

@Database(
    entities = [WeatherEntity::class, ConfigItemEntity::class],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDao(): WeatherDao

    abstract fun configDao(): ConfigDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor :
    RoomDatabaseConstructor<AppDatabase> {

    override fun initialize(): AppDatabase
}