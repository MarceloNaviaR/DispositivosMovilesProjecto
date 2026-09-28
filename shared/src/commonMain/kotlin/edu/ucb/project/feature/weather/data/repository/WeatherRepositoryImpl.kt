package edu.ucb.project.feature.weather.data.repository


import edu.ucb.project.feature.weather.data.datasource.WeatherRemoteDataSource
import edu.ucb.project.feature.weather.data.mapper.toDomain
import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.domain.repository.WeatherRepository



class WeatherRepositoryImpl(

    private val dataSource: WeatherRemoteDataSource

): WeatherRepository {


    override suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Weather {


        return dataSource
            .getWeather(
                latitude,
                longitude
            )
            .toDomain()


    }


}