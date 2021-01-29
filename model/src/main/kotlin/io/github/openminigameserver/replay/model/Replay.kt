package io.github.openminigameserver.replay.model

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.time.Duration

data class Replay(
    override var version: Int = 4,
    override var id: UUID = UUID.randomUUID(),
    override var recordStartTime: Instant = now(),
    val entities: MutableMap<Int, RecordableEntity> = mutableMapOf(),
    val actions: ConcurrentLinkedDeque<RecordableAction> = ConcurrentLinkedDeque<RecordableAction>()
) : ReplayHeader(version, id, recordStartTime) {

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
