package edu.ucb.project.feature.weather.presentation.screen


import androidx.compose.runtime.*
import org.koin.compose.viewmodel.koinViewModel

import edu.ucb.project.feature.weather.presentation.viewmodel.WeatherViewModel
import edu.ucb.project.feature.weather.presentation.composable.WeatherContent



@Composable
fun WeatherScreen(

    viewModel: WeatherViewModel = koinViewModel()

){


    val state by viewModel.state.collectAsState()



    LaunchedEffect(Unit){

        viewModel.loadWeather(

            latitude = -17.3935,
            longitude = -66.1570

        )

    }



    WeatherContent(

        weather = state.weather,

        loading = state.loading

    )

}