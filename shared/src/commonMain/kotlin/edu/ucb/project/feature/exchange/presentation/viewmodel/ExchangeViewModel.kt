package edu.ucb.project.feature.exchange.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ucb.project.feature.exchange.domain.usecase.ObserveExchangeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExchangeViewModel(
    private val useCase: ObserveExchangeUseCase
) : ViewModel() {

    private val _message =
        MutableStateFlow("")

    val message =
        _message.asStateFlow()

    init {
        observeMessage()
    }

    private fun observeMessage() {

        viewModelScope.launch {

            useCase()
                .collect { message ->

                    _message.value =
                        message ?: "Sin información"
                }
        }
    }
}