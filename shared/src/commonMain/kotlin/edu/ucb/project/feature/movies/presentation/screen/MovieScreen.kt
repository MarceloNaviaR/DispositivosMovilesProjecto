package edu.ucb.project.feature.movies.presentation.screen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import org.koin.compose.viewmodel.koinViewModel

import edu.ucb.project.feature.movies.presentation.composable.MovieContent
import edu.ucb.project.feature.movies.presentation.viewmodel.MovieViewModel



@Composable
fun MovieScreen(

    viewModel: MovieViewModel = koinViewModel()

){


    val state by viewModel.state.collectAsState()



    LaunchedEffect(Unit){

        viewModel.loadMovies()

    }



    MovieContent(

        movies = state.movies

    )


}