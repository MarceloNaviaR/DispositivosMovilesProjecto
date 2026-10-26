package edu.ucb.project.roomastruth.domain.VO

/** Clave de configuración válida: no vacía, sin espacios, máx. 50 caracteres. */
data class ConfigKey private constructor(val value: String) {
    companion object {
        fun create(raw: String): Result<ConfigKey> {
            val key = raw.trim()
            return when {
                key.isEmpty() -> Result.failure(IllegalArgumentException("La clave no puede estar vacía"))
                key.any { it.isWhitespace() } -> Result.failure(IllegalArgumentException("La clave no puede tener espacios"))
                key.length > 50 -> Result.failure(IllegalArgumentException("La clave es muy larga (máx. 50)"))
                else -> Result.success(ConfigKey(key))
            }
        }
    }
}
