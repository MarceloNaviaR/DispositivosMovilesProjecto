package edu.ucb.project.roomastruth.data.repository

import edu.ucb.project.roomastruth.data.datasource.ConfigLocalDataSource
import edu.ucb.project.roomastruth.data.mapper.toDomain
import edu.ucb.project.roomastruth.data.mapper.toEntity
import edu.ucb.project.roomastruth.domain.model.ConfigItem
import edu.ucb.project.roomastruth.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigRepositoryImpl(
    private val local: ConfigLocalDataSource
) : ConfigRepository {

    override fun observeAll(): Flow<List<ConfigItem>> =
        local.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(item: ConfigItem) = local.insert(item.toEntity())

    override suspend fun delete(id: Long) = local.delete(id)

    override suspend fun deleteAll() = local.deleteAll()
}
