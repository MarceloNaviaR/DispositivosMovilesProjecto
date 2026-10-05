package edu.ucb.project.feature.exchange.domain.repository

import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {

    suspend fun observe(): Flow<String?>
}