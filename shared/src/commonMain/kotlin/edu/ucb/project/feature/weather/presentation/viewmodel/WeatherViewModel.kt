package edu.ucb.project.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import edu.ucb.project.feature.weather.domain.usecase.GetWeatherUseCase
import edu.ucb.project.feature.weather.presentation.state.WeatherState

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()

    fun loadWeather(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {

            _state.value = WeatherState(
                loading = true
            )

            try {
                val result = getWeatherUseCase(
                    latitude,
                    longitude
                )

                _state.value = WeatherState(
                    weather = result,
                    loading = false,
                    error = null
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _state.value = WeatherState(
                    weather = null,
                    loading = false,
                    error = e.message
                        ?: "No se pudo consultar el clima"
                )
            }
        }
    }
}
