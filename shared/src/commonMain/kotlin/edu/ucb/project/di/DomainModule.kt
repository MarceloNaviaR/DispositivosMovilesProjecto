package edu.ucb.project.di
import edu.ucb.project.feature.auth.login.domain.usecase.LoginUseCase
import org.koin.dsl.module
import edu.ucb.project.feature.weather.domain.usecase.GetWeatherUseCase

val domainModule = module {


    single {

        LoginUseCase(
            get()
        )

    }

    single {

        GetWeatherUseCase(
            get()
        )

    }



}