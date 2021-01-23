package io.github.openminigameserver.replay.model

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

data class Replay(
    val version: Int = 1,
    val id: UUID = UUID.randomUUID(),
    val recordStartTime: Instant = now(),
    val actions: MutableList<RecordableAction> = mutableListOf(),
    val entities: MutableMap<Int, RecordableEntity> = mutableMapOf()
) {
    var duration: Duration = Duration.ZERO

    val currentDuration: Duration
        get() {
            return now().minus(recordStartTime)
        }

    fun addAction(action: RecordableAction) {
        action.timestamp = currentDuration
        actions.add(action)
    }

    fun getEntityById(id: Int): RecordableEntity {
        return entities[id] ?: throw Exception("Unable to find entity with id $id")
    }

}
