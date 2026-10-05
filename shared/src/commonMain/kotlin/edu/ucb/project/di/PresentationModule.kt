package edu.ucb.project.di

import edu.ucb.project.feature.auth.login.presentation.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import edu.ucb.project.feature.weather.presentation.viewmodel.WeatherViewModel
import edu.ucb.project.feature.exchange.presentation.viewmodel.ExchangeViewModel
val presentationModule = module {


    viewModel {

        LoginViewModel(
            get()
        )

    }

    viewModel {

        WeatherViewModel(
            get()
        )

    }

    viewModel {
        ExchangeViewModel(
            get()
        )
    }

}