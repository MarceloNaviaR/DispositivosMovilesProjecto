package edu.ucb.project.feature.weather.data.datasource

import edu.ucb.project.feature.weather.data.dao.WeatherDao
import edu.ucb.project.feature.weather.data.entity.WeatherEntity
import edu.ucb.project.feature.weather.domain.model.Weather

class WeatherLocalDataSource(
    private val dao: WeatherDao
) {

    suspend fun getWeather(): Weather? {

        return dao
            .getList()
            .firstOrNull()
            ?.toModel()
    }

    suspend fun insert(weather: Weather) {

        dao.insert(
            weather.toEntity()
        )
    }

    suspend fun deleteAll() {

        dao.deleteAll()
    }

    private fun WeatherEntity.toModel(): Weather {

        return Weather(
            latitude = latitude,
            longitude = longitude,
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = windDirection,
            weatherCode = weatherCode,
            time = time
        )
    }

    private fun Weather.toEntity(): WeatherEntity {

        return WeatherEntity(
            id = 1,
            latitude = latitude,
            longitude = longitude,
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = windDirection,
            weatherCode = weatherCode,
            time = time
        )
    }
}