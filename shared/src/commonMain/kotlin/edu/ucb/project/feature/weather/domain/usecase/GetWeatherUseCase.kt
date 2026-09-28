package edu.ucb.project.feature.weather.domain.usecase


import edu.ucb.project.feature.weather.domain.repository.WeatherRepository



class GetWeatherUseCase(

    private val repository: WeatherRepository

){


    suspend operator fun invoke(

        latitude: Double,

        longitude: Double

    ) = repository.getWeather(
        latitude,
        longitude
    )


}