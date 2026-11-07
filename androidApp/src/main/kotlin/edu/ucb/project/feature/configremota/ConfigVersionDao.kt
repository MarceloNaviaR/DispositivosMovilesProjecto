package edu.ucb.project.feature.configremota

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigVersionDao {

    @Insert
    suspend fun insertar(
        config: ConfigVersionEntity
    ): Long

    @Query(
        "SELECT * FROM config_versions " +
                "ORDER BY id DESC"
    )
    fun obtenerHistorial(): Flow<List<ConfigVersionEntity>>

    @Query(
        "SELECT * FROM config_versions " +
                "ORDER BY id DESC LIMIT 1"
    )
    fun observarUltima(): Flow<ConfigVersionEntity?>

    @Query(
        "SELECT * FROM config_versions " +
                "ORDER BY id DESC LIMIT 1"
    )
    suspend fun obtenerUltima(): ConfigVersionEntity?

    @Query(
        "SELECT * FROM config_versions " +
                "WHERE synced = 0 ORDER BY id ASC"
    )
    suspend fun obtenerPendientes(): List<ConfigVersionEntity>

    @Query(
        "UPDATE config_versions SET " +
                "synced = 1, remoteVersion = :version " +
                "WHERE id = :id"
    )
    suspend fun marcarSincronizada(
        id: Long,
        version: Long
    )

    @Query(
        "UPDATE config_versions " +
                "SET notified = 1 WHERE id = :id"
    )
    suspend fun marcarNotificada(id: Long)

    @Query(
        "DELETE FROM config_versions"
    )
    suspend fun eliminarHistorial()
}
