package edu.ucb.project.feature.weather.data.datasource


import edu.ucb.project.feature.weather.data.dto.WeatherDto



interface WeatherRemoteDataSource {


    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherDto


}