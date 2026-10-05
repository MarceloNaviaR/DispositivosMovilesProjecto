package edu.ucb.project.feature.exchange.domain.usecase

import edu.ucb.project.feature.exchange.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow

class ObserveExchangeUseCase(
    private val repository: ExchangeRepository
) {

    suspend operator fun invoke(): Flow<String?> {

        return repository.observe()
    }
}