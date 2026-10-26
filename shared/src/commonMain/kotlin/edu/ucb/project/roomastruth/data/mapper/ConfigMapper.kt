package edu.ucb.project.roomastruth.data.mapper

import edu.ucb.project.roomastruth.data.entity.ConfigItemEntity
import edu.ucb.project.roomastruth.domain.model.ConfigItem

fun ConfigItemEntity.toDomain() = ConfigItem(id = id, clave = clave, valor = valor, timestamp = timestamp)

fun ConfigItem.toEntity() = ConfigItemEntity(id = id, clave = clave, valor = valor, timestamp = timestamp)
