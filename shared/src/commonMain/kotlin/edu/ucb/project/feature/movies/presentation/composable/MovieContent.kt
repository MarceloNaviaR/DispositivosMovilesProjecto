package edu.ucb.project.feature.movies.presentation.composable


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import edu.ucb.project.feature.movies.domain.model.Movie



@Composable
fun MovieContent(

    movies: List<Movie>

){


    LazyColumn {


        items(movies){ movie ->



            Column {


                Text(
                    text = movie.title
                )


                Text(
                    text = movie.overview
                )


            }


        }


    }


}