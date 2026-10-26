package edu.ucb.project.roomastruth.data.datasource

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlin.time.Clock

/**
 * Siembra 3 registros de ejemplo SOLO cuando la base de datos se crea por primera vez.
 * No se re-siembra si el usuario borra todo, porque onCreate no se vuelve a ejecutar.
 */
class ConfigSeedCallback : RoomDatabase.Callback() {

    override suspend fun onCreate(connection: SQLiteConnection) {
        seed(connection)
    }

    // Tras una migración destructiva la BD se recrea desde cero: también cuenta como primer arranque.
    override suspend fun onDestructiveMigration(connection: SQLiteConnection) {
        seed(connection)
    }

    private fun seed(connection: SQLiteConnection) {
        val now = Clock.System.now().toEpochMilliseconds()
        connection.execSQL("INSERT INTO config_item (clave, valor, timestamp) VALUES ('tema', 'oscuro', $now)")
        connection.execSQL("INSERT INTO config_item (clave, valor, timestamp) VALUES ('idioma', 'es', ${now - 1})")
        connection.execSQL("INSERT INTO config_item (clave, valor, timestamp) VALUES ('notificaciones', 'activadas', ${now - 2})")
    }
}
