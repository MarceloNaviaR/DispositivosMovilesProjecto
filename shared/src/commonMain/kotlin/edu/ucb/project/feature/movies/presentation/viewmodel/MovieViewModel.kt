package edu.ucb.project.feature.movies.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ucb.project.feature.movies.domain.usecase.GetMoviesUseCase
import edu.ucb.project.feature.movies.presentation.state.MovieState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MovieViewModel(
    private val getMoviesUseCase: GetMoviesUseCase
): ViewModel(){


    private val _state =
        MutableStateFlow(MovieState())


    val state =
        _state.asStateFlow()



    fun loadMovies(){


        viewModelScope.launch {


            _state.value =
                _state.value.copy(
                    isLoading = true
                )


            val movies =
                getMoviesUseCase()


            _state.value =
                _state.value.copy(
                    movies = movies,
                    isLoading = false
                )


        }

    }

}
