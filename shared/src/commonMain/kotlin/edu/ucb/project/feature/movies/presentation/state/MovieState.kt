package edu.ucb.project.feature.movies.presentation.state


import edu.ucb.project.feature.movies.data.data.model.Movie


data class MovieState(

    val movies: List<Movie> = emptyList(),

    val isLoading: Boolean = false,

    val error: String? = null

)