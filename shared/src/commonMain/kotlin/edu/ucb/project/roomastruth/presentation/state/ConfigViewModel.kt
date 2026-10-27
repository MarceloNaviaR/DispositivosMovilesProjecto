package edu.ucb.project.roomastruth.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ucb.project.roomastruth.domain.usecase.AddConfigUseCase
import edu.ucb.project.roomastruth.domain.usecase.DeleteAllConfigsUseCase
import edu.ucb.project.roomastruth.domain.usecase.DeleteConfigUseCase
import edu.ucb.project.roomastruth.domain.usecase.ObserveConfigsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfigViewModel(
    observeConfigs: ObserveConfigsUseCase,
    private val addConfig: AddConfigUseCase,
    private val deleteConfig: DeleteConfigUseCase,
    private val deleteAllConfigs: DeleteAllConfigsUseCase
) : ViewModel() {

    private data class Form(
        val key: String = "",
        val value: String = "",
        val error: String? = null
    )

    private val form = MutableStateFlow(Form())

    // Room es la única fuente de verdad: la lista viene SOLO del Flow de la base de datos.
    // Cualquier insert/delete en Room vuelve a emitir y la UI se actualiza sola.
    val state: StateFlow<ConfigUiState> =
        combine(observeConfigs(), form) { items, f ->
            ConfigUiState(
                items = items,
                isLoading = false,
                keyInput = f.key,
                valueInput = f.value,
                error = f.error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConfigUiState()
        )

    fun onKeyChange(text: String) = form.update { it.copy(key = text, error = null) }

    fun onValueChange(text: String) = form.update { it.copy(value = text, error = null) }

    fun onAddClick() {
        val current = form.value
        viewModelScope.launch {
            addConfig(current.key, current.value)
                .onSuccess { form.value = Form() }
                .onFailure { e -> form.update { it.copy(error = e.message) } }
        }
    }

    fun onDelete(id: Long) {
        viewModelScope.launch { deleteConfig(id) }
    }

    fun onDeleteAll() {
        viewModelScope.launch { deleteAllConfigs() }
    }
}
