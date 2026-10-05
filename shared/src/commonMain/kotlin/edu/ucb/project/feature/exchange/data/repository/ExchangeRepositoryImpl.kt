package edu.ucb.project.feature.exchange.data.repository

import edu.ucb.project.feature.exchange.data.datasource.RealTimeDataBase
import edu.ucb.project.feature.exchange.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow

class ExchangeRepositoryImpl(
    private val realTimeDataBase: RealTimeDataBase
) : ExchangeRepository {

    override suspend fun observe(): Flow<String?> {

        return realTimeDataBase.observeMessage()
    }
}