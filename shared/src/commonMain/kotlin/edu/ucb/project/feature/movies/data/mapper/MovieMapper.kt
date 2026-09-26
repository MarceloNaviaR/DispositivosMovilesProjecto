package edu.ucb.project.feature.movies.data.mapper


import edu.ucb.project.feature.movies.data.dto.MovieDto
import edu.ucb.project.feature.movies.domain.model.Movie



fun MovieDto.toDomain(): Movie {


    return Movie(

        id = id,

        title = title,

        overview = overview,

        poster = posterPath

    )

}