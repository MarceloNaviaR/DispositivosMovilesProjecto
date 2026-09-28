package edu.ucb.project.di

import edu.ucb.project.feature.auth.login.domain.repository.AuthRepository
import org.koin.dsl.module
import edu.ucb.project.feature.weather.data.datasource.WeatherRemoteDataSource
import edu.ucb.project.feature.weather.data.service.WeatherApiService
import edu.ucb.project.feature.weather.data.repository.WeatherRepositoryImpl
import edu.ucb.project.feature.weather.domain.repository.WeatherRepository

val dataModule = module {


    single<AuthRepository>{

        object: AuthRepository {


            override suspend fun login(
                username:String,
                password:String
            ) = null


        }

    }

    single<WeatherRemoteDataSource>{

        WeatherApiService()

    }



    single<WeatherRepository>{

        WeatherRepositoryImpl(
            get()
        )

    }

}