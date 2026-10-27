package edu.ucb.project.roomastruth.domain.repository

import edu.ucb.project.roomastruth.domain.model.ConfigItem
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun observeAll(): Flow<List<ConfigItem>>
    suspend fun add(item: ConfigItem)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}
