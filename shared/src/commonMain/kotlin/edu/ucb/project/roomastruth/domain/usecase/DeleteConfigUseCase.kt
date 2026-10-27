package edu.ucb.project.roomastruth.domain.usecase

import edu.ucb.project.roomastruth.domain.repository.ConfigRepository

class DeleteConfigUseCase(private val repository: ConfigRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
