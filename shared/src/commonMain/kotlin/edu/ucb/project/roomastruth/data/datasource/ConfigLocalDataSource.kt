package edu.ucb.project.roomastruth.data.datasource

import edu.ucb.project.roomastruth.data.dao.ConfigDao
import edu.ucb.project.roomastruth.data.entity.ConfigItemEntity
import kotlinx.coroutines.flow.Flow

class ConfigLocalDataSource(private val dao: ConfigDao) {
    fun observeAll(): Flow<List<ConfigItemEntity>> = dao.getAll()
    suspend fun insert(item: ConfigItemEntity) = dao.insert(item)
    suspend fun delete(id: Long) = dao.deleteById(id)
    suspend fun deleteAll() = dao.deleteAll()
}
