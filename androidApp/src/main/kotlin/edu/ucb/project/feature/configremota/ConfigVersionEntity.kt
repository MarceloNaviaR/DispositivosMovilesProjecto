package edu.ucb.project.feature.configremota

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config_versions")
data class ConfigVersionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mensaje: String,
    val limite: Int,
    val featureEnabled: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false,
    val notified: Boolean = false,
    val remoteVersion: Long = 0
)
