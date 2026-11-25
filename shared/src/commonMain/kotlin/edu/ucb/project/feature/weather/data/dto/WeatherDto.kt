package edu.ucb.project.feature.weather.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val latitude: Double,
    val longitude: Double,
    val current_weather: CurrentWeatherDto? = null,
    val current: CurrentConditionsDto? = null,
    val hourly: HourlyWeatherDto? = null,
    val daily: DailyWeatherDto? = null
)

@Serializable
data class CurrentWeatherDto(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String,
    val is_day: Int? = null
)

@Serializable
data class CurrentConditionsDto(
    val precipitation: Double? = null,
    val is_day: Int? = null
)

@Serializable
data class HourlyWeatherDto(
    val time: List<String> = emptyList(),
    val temperature_2m: List<Double?> = emptyList(),
    val precipitation: List<Double?> = emptyList(),
    val weather_code: List<Int?> = emptyList(),
    val is_day: List<Int?> = emptyList()
)

@Serializable
data class DailyWeatherDto(
    val time: List<String> = emptyList(),
    val temperature_2m_max: List<Double?> = emptyList(),
    val temperature_2m_min: List<Double?> = emptyList(),
    val weather_code: List<Int?> = emptyList(),
    val sunrise: List<String?> = emptyList(),
    val sunset: List<String?> = emptyList()
)
