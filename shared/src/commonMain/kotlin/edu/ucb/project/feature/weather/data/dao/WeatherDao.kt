package edu.ucb.project.feature.weather.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import edu.ucb.project.feature.weather.data.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather")
    suspend fun getList(): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherEntity)

    @Query("DELETE FROM weather")
    suspend fun deleteAll()
}