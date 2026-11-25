package edu.ucb.project.feature.weather.domain.model

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val precipitationMm: Double? = null,
    val weatherCode: Int = 0,
    val isDay: Boolean? = null
)

data class DailyWeather(
    val date: String,
    val maximum: Double,
    val minimum: Double,
    val weatherCode: Int,
    val sunrise: String? = null,
    val sunset: String? = null
)

data class Weather(
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val weatherCode: Int,
    val time: String,

    // Compatibilidad con los indicadores previos
    val precipitationMm: Double? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),

    // Nuevos datos del laboratorio
    val dailyForecast: List<DailyWeather> = emptyList(),
    val isDay: Boolean? = null
)
