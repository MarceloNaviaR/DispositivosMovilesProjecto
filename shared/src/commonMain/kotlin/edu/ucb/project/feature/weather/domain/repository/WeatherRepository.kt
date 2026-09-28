package edu.ucb.project.feature.weather.domain.repository


import edu.ucb.project.feature.weather.domain.model.Weather


interface WeatherRepository {


    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Weather


}