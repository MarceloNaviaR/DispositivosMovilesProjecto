package edu.ucb.project.feature.weather.presentation.state


import edu.ucb.project.feature.weather.domain.model.Weather


data class WeatherState(

    val weather: Weather? = null,

    val loading:Boolean = false,

    val error:String? = null

)