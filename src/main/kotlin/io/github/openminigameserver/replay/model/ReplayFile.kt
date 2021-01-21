package io.github.openminigameserver.replay.model

import io.github.openminigameserver.replay.extensions.currentDuration
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import net.minestom.server.entity.Entity
import java.util.*

data class ReplayFile(
    val version: Int = 1,
    val id: UUID = UUID.randomUUID(),
    val recordStartTime: Instant = now(),
    val actions: MutableList<RecordableAction> = mutableListOf(),
    val entities: MutableMap<Int, RecordableEntity> = mutableMapOf()
) {
    fun addAction(action: RecordableAction) {
        action.timestamp = currentDuration
        actions.add(action)
    }
    fun getEntityById(id: Int): RecordableEntity {
        return entities[id] ?: throw Exception("Unable to find entity with id $id")
    }
    fun getEntity(entity: Entity): RecordableEntity {
        return getEntityById(entity.entityId)
    }

}
