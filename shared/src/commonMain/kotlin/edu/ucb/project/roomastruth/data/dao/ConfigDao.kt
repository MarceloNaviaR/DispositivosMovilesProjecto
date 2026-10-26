package edu.ucb.project.roomastruth.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import edu.ucb.project.roomastruth.data.entity.ConfigItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    // Flow: Room vuelve a emitir cada vez que cambia la tabla.
    @Query("SELECT * FROM config_item ORDER BY timestamp DESC, id DESC")
    fun getAll(): Flow<List<ConfigItemEntity>>

    // REPLACE: si la clave ya existe, se actualiza su valor.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ConfigItemEntity)

    @Query("DELETE FROM config_item WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM config_item")
    suspend fun deleteAll()
}
