package edu.ucb.project.feature.movies.data.repository


import edu.ucb.project.feature.movies.data.datasource.MovieRemoteDataSource
import edu.ucb.project.feature.movies.data.mapper.toDomain
import edu.ucb.project.feature.movies.domain.model.Movie
import edu.ucb.project.feature.movies.domain.repository.MovieRepository



class MovieRepositoryImpl(

    private val dataSource: MovieRemoteDataSource

): MovieRepository {



    override suspend fun getMovies(): List<Movie>{


        return dataSource
            .getMovies()
            .map {

                it.toDomain()

            }

    }


}