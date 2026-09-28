package edu.ucb.project.feature.weather.domain.model


data class Weather(

    val latitude: Double,

    val longitude: Double,

    val temperature: Double,

    val windSpeed: Double,

    val windDirection: Double,

    val weatherCode: Int,

    val time: String

)