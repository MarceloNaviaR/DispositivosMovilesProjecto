package edu.ucb.project.feature.movies.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MovieDto(

    val id: Int,

    val title: String,

    val overview: String,

    @SerialName("poster_path")
    val posterPath: String?

)