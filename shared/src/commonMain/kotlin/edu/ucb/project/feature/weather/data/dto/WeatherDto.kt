package edu.ucb.project.feature.weather.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class WeatherDto(

    val latitude: Double,

    val longitude: Double,

    val current_weather: CurrentWeatherDto?

)


@Serializable
data class CurrentWeatherDto(

    val temperature: Double,

    val windspeed: Double,

    val winddirection: Double,

    val weathercode: Int,

    val time: String

)