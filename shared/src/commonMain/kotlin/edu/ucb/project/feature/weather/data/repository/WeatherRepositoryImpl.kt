package edu.ucb.project.feature.weather.data.repository

import edu.ucb.project.feature.weather.data.datasource.WeatherLocalDataSource
import edu.ucb.project.feature.weather.data.datasource.WeatherRemoteDataSource
import edu.ucb.project.feature.weather.data.mapper.toDomain
import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.domain.repository.WeatherRepository

class WeatherRepositoryImpl(

    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource

) : WeatherRepository {

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Weather {

        return remoteDataSource
            .getWeather(
                latitude,
                longitude
            )
            .toDomain()
    }
}