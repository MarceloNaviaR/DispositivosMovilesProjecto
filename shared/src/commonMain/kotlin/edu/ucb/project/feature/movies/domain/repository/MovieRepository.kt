package edu.ucb.project.feature.movies.domain.repository


import edu.ucb.project.feature.movies.data.dto.MovieDto


interface MovieRepository {


    suspend fun getMovies(): List<MovieDto>


}