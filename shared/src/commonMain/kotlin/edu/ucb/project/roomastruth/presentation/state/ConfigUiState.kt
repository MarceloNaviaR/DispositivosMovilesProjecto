package edu.ucb.project.roomastruth.presentation.state

import edu.ucb.project.roomastruth.domain.model.ConfigItem

data class ConfigUiState(
    val items: List<ConfigItem> = emptyList(),
    val isLoading: Boolean = true,
    val keyInput: String = "",
    val valueInput: String = "",
    val error: String? = null
)
