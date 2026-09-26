package edu.ucb.project.feature.movies.domain.usecase


import edu.ucb.project.feature.movies.domain.repository.MovieRepository


class GetMoviesUseCase(
    private val repository: MovieRepository
) {


    suspend operator fun invoke() = repository.getMovies()


}