package io.github.openminigameserver.replay.model.recordable.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class RecordableEntity(
    val id: Int,
    val type: String,
    val spawnPosition: RecordablePosition?,
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