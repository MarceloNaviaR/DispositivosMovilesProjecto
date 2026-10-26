package edu.ucb.project.roomastruth.data.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** Entidad Room: ConfigItem(id, clave, valor, timestamp). La clave es única. */
@Entity(
    tableName = "config_item",
    indices = [Index(value = ["clave"], unique = true)]
)
data class ConfigItemEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "clave")
    val clave: String,

    @ColumnInfo(name = "valor")
    val valor: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
