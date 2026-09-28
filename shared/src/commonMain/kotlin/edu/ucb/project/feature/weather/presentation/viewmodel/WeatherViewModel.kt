package edu.ucb.project.feature.weather.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import edu.ucb.project.feature.weather.domain.usecase.GetWeatherUseCase
import edu.ucb.project.feature.weather.presentation.state.WeatherState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class WeatherViewModel(

    private val getWeatherUseCase: GetWeatherUseCase

):ViewModel(){



    private val _state =
        MutableStateFlow(
            WeatherState()
        )


    val state =
        _state.asStateFlow()



    fun loadWeather(

        latitude:Double,

        longitude:Double

    ){


        viewModelScope.launch {


            _state.value =
                _state.value.copy(
                    loading = true
                )


            val result =
                getWeatherUseCase(
                    latitude,
                    longitude
                )


            _state.value =
                _state.value.copy(

                    weather = result,

                    loading = false

                )


        }


    }



}