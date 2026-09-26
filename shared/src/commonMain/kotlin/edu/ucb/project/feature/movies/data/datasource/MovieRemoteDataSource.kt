package edu.ucb.project.feature.movies.data.datasource


import edu.ucb.project.feature.movies.data.dto.MovieDto



interface MovieRemoteDataSource {


    suspend fun getMovies(): List<MovieDto>


}