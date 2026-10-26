package edu.ucb.project.roomastruth.domain.usecase

import edu.ucb.project.roomastruth.domain.repository.ConfigRepository

class DeleteAllConfigsUseCase(private val repository: ConfigRepository) {
    suspend operator fun invoke() = repository.deleteAll()
}
