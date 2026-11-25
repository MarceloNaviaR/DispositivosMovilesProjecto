package edu.ucb.project.feature.weather.presentation

import edu.ucb.project.feature.weather.domain.model.Weather

data class WeatherRecommendation(
    val icon: String,
    val title: String,
    val message: String
)

fun getWeatherRecommendation(
    weather: Weather
): WeatherRecommendation {

    val temperature = weather.temperature
    val windSpeed = weather.windSpeed
    val code = weather.weatherCode

    return when {

        // Tormentas
        code in 95..99 -> WeatherRecommendation(
            icon = "⛈️",
            title = "Precaución por tormentas",
            message = "Evita salir si no es necesario. " +
                    "Busca refugio y evita espacios abiertos."
        )

        // Lluvia intensa o granizo de nieve
        code in 65..67 || code in 81..82 ||
                code in 75..77 || code in 85..86 ->
            WeatherRecommendation(
                icon = "🌧️",
                title = "Lluvia o precipitación intensa",
                message = "Lleva paraguas y chaqueta " +
                        "impermeable. Evita zonas inundadas."
            )

        // Otras precipitaciones
        code in 51..64 || code in 71..74 ||
                code in 80..80 || code in 83..84 ->
            WeatherRecommendation(
                icon = "🌦️",
                title = "Posibles precipitaciones",
                message = "Lleva un paraguas o impermeable " +
                        "y utiliza calzado adecuado."
            )

        // Niebla
        code in 45..48 -> WeatherRecommendation(
            icon = "🌫️",
            title = "Hay niebla",
            message = "Sal con precaución. Si conduces, " +
                    "reduce la velocidad y mejora tu visibilidad."
        )

        // Viento
        windSpeed >= 40 -> WeatherRecommendation(
            icon = "💨",
            title = "Viento fuerte",
            message = "Usa una chaqueta cortavientos " +
                    "y evita permanecer cerca de objetos inestables."
        )

        // Temperatura baja
        temperature < 10 -> WeatherRecommendation(
            icon = "🧥",
            title = "Hace bastante frío",
            message = "Sal con abrigo, bufanda y ropa " +
                    "que te proteja de las bajas temperaturas."
        )

        // Temperatura elevada
        temperature >= 30 -> WeatherRecommendation(
            icon = "☀️",
            title = "Hace mucho calor",
            message = "Lleva agua, usa protector solar " +
                    "y evita exponerte mucho tiempo al sol."
        )

        // Temperatura agradable
        temperature in 18.0..29.99 -> WeatherRecommendation(
            icon = "😊",
            title = "Clima agradable",
            message = "Puedes salir con ropa cómoda. " +
                    "Lleva agua y protección solar si hay sol."
        )

        // Temperatura fresca
        else -> WeatherRecommendation(
            icon = "🌤️",
            title = "Clima fresco",
            message = "Lleva una chaqueta ligera " +
                    "para mantenerte cómodo al salir."
        )
    }
}
