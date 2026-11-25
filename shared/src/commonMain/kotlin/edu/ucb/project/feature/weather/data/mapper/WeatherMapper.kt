package edu.ucb.project.feature.weather.data.mapper

import edu.ucb.project.feature.weather.data.dto.WeatherDto
import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.domain.model.HourlyWeather
import edu.ucb.project.feature.weather.domain.model.DailyWeather

fun WeatherDto.toDomain(): Weather {

    val actual = current_weather
    val referenceHour = actual?.time?.take(13).orEmpty()

    val hourlyRecords = hourly?.let { source ->

        source.time.indices.mapNotNull { index ->
            val time = source.time[index]
            val value = source.temperature_2m.getOrNull(index)

            if (
                value == null ||
                !value.isFinite() ||
                (
                        referenceHour.isNotEmpty() &&
                                time.take(13) < referenceHour
                        )
            ) {
                null
            } else {
                HourlyWeather(
                    time = time,
                    temperature = value,
                    precipitationMm =
                        source.precipitation.getOrNull(index)
                            ?.takeIf { it.isFinite() },
                    weatherCode =
                        source.weather_code.getOrNull(index)
                            ?: actual?.weathercode ?: 0,
                    isDay = source.is_day.getOrNull(index)
                        ?.let { it == 1 }
                )
            }
        }.take(24)

    } ?: emptyList()

    val dailyRecords = daily?.let { source ->

        source.time.indices.mapNotNull { index ->
            val maximum =
                source.temperature_2m_max.getOrNull(index)

            val minimum =
                source.temperature_2m_min.getOrNull(index)

            if (
                maximum == null ||
                minimum == null ||
                !maximum.isFinite() ||
                !minimum.isFinite()
            ) {
                null
            } else {
                DailyWeather(
                    date = source.time[index],
                    maximum = maximum,
                    minimum = minimum,
                    weatherCode =
                        source.weather_code.getOrNull(index) ?: 0,
                    sunrise = source.sunrise.getOrNull(index),
                    sunset = source.sunset.getOrNull(index)
                )
            }
        }.take(7)

    } ?: emptyList()

    return Weather(
        latitude = latitude,
        longitude = longitude,
        temperature = actual?.temperature ?: 0.0,
        windSpeed = actual?.windspeed ?: 0.0,
        windDirection = actual?.winddirection ?: 0.0,
        weatherCode = actual?.weathercode ?: 0,
        time = actual?.time.orEmpty(),

        precipitationMm =
            current?.precipitation
                ?.takeIf { it.isFinite() },

        hourlyForecast = hourlyRecords,
        dailyForecast = dailyRecords,

        isDay = (current?.is_day ?: actual?.is_day)
            ?.let { it == 1 }
    )
}
