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


        install(ContentNegotiation){


            json(

                Json {

                    ignoreUnknownKeys = true

                    prettyPrint = true

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
        ){


            parameter(
                "latitude",
                latitude
            )


            parameter(
                "longitude",
                longitude
            )


            parameter(
                "current_weather",
                true
            )


        }.body()


    }


}