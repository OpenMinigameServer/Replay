package io.github.openminigameserver.replay.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.time.Duration

data class Replay(
    val version: Int = 3,
    val id: UUID = UUID.randomUUID(),
    val recordStartTime: Instant = now(),
    val entities: MutableMap<Int, RecordableEntity> = mutableMapOf(),
    val actions: ConcurrentLinkedDeque<RecordableAction> = ConcurrentLinkedDeque<RecordableAction>()
) {
    private val metadata: MutableMap<String, Any> = mutableMapOf()

    operator fun <T : Any> get(name: String): T? {
        return metadata[name] as? T
    }
    operator fun <T : Any> set(name: String, value: T) {
        metadata[name] = value
    }

    var duration: Duration = Duration.ZERO

    @get:JsonIgnore
    val currentDuration: Duration
        get() {
            return now().minus(recordStartTime)
        }

    fun addAction(action: RecordableAction) {
        action.timestamp = currentDuration
        actions.add(action)
    }

    fun getEntityById(id: Int): RecordableEntity? {
        return entities[id]
    }

}
