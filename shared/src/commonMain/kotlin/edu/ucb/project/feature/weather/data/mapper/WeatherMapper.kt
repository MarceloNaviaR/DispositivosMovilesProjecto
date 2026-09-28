package edu.ucb.project.feature.weather.data.mapper


import edu.ucb.project.feature.weather.data.dto.WeatherDto
import edu.ucb.project.feature.weather.domain.model.Weather



fun WeatherDto.toDomain(): Weather {


    return Weather(

        latitude = latitude,

        longitude = longitude,

        temperature =
            current_weather?.temperature ?: 0.0,


        windSpeed =
            current_weather?.windspeed ?: 0.0,


        windDirection =
            current_weather?.winddirection ?: 0.0,


        weatherCode =
            current_weather?.weathercode ?: 0,


        time =
            current_weather?.time ?: ""

    )

}