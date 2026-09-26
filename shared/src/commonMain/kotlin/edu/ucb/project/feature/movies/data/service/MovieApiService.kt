package edu.ucb.project.feature.movies.data.service


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

import kotlinx.serialization.json.Json

import edu.ucb.project.feature.movies.data.datasource.MovieRemoteDataSource
import edu.ucb.project.feature.movies.data.dto.MovieDto



class MovieApiService: MovieRemoteDataSource {


    private val client =
        HttpClient {


            install(ContentNegotiation){


                json(

                    Json {

                        prettyPrint = true

                        ignoreUnknownKeys = true

                    }

                )

            }

        }



    override suspend fun getMovies(): List<MovieDto>{


        val response =
            client.get(
                "URL_DE_TU_API"
            )


        return response.body()


    }


}