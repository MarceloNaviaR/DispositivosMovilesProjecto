package edu.ucb.project.roomastruth.domain.usecase

import edu.ucb.project.roomastruth.domain.VO.ConfigKey
import edu.ucb.project.roomastruth.domain.model.ConfigItem
import edu.ucb.project.roomastruth.domain.repository.ConfigRepository
import kotlin.time.Clock

class AddConfigUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke(clave: String, valor: String): Result<Unit> {
        val key = ConfigKey.create(clave).getOrElse { return Result.failure(it) }
        if (valor.isBlank()) {
            return Result.failure(IllegalArgumentException("El valor no puede estar vacío"))
        }
        repository.add(
            ConfigItem(
                clave = key.value,
                valor = valor.trim(),
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
        return Result.success(Unit)
    }
}
