package edu.ucb.project.feature.weather.data.service

import edu.ucb.project.feature.weather.data.datasource.WeatherRemoteDataSource
import edu.ucb.project.feature.weather.data.dto.WeatherDto

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

import kotlinx.serialization.json.Json

class WeatherApiService : WeatherRemoteDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherDto {

        return client.get(
            "https://api.open-meteo.com/v1/forecast"
        ) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)

            parameter("current_weather", true)

            parameter(
                "current",
                "precipitation,is_day"
            )

            parameter(
                "hourly",
                "temperature_2m,precipitation,weather_code,is_day"
            )

            parameter(
                "daily",
                "temperature_2m_max,temperature_2m_min,weather_code,sunrise,sunset"
            )

            parameter("forecast_days", 7)
            parameter("timezone", "auto")
        }.body()
    }
}
