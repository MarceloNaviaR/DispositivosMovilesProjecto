package edu.ucb.project.di

import edu.ucb.project.core.database.AppDatabase
import edu.ucb.project.feature.auth.login.domain.repository.AuthRepository
import edu.ucb.project.feature.weather.data.dao.WeatherDao
import edu.ucb.project.feature.weather.data.datasource.WeatherLocalDataSource
import edu.ucb.project.feature.weather.data.datasource.WeatherRemoteDataSource
import edu.ucb.project.feature.weather.data.repository.WeatherRepositoryImpl
import edu.ucb.project.feature.weather.data.service.WeatherApiService
import edu.ucb.project.feature.weather.domain.repository.WeatherRepository
import edu.ucb.project.feature.exchange.data.datasource.RealTimeDataBase
import edu.ucb.project.feature.exchange.data.repository.ExchangeRepositoryImpl
import edu.ucb.project.feature.exchange.domain.repository.ExchangeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {

    single<AuthRepository> {

        object : AuthRepository {

            override suspend fun login(
                username: String,
                password: String
            ) = null
        }
    }

    single<WeatherDao> {
        get<AppDatabase>().getDao()
    }

    single {
        WeatherLocalDataSource(
            dao = get()
        )
    }

    single<WeatherRemoteDataSource> {
        WeatherApiService()
    }
    single<WeatherRepository> {
        WeatherRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }

    singleOf(::RealTimeDataBase)

    single<ExchangeRepository> {
        ExchangeRepositoryImpl(
            get()
        )
    }
}