package edu.ucb.project.roomastruth.domain.usecase

import edu.ucb.project.roomastruth.domain.model.ConfigItem
import edu.ucb.project.roomastruth.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow

class ObserveConfigsUseCase(private val repository: ConfigRepository) {
    operator fun invoke(): Flow<List<ConfigItem>> = repository.observeAll()
}
