package io.github.openminigameserver.replay.model.recordable.entity

import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData

data class RecordableEntity(
    val id: Int,
    val type: String,
    val spawnPosition: RecordablePositionAndVector?,
    val entityData: BaseEntityData? = null
) {
    var spawnOnStart: Boolean = spawnPosition != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordableEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}