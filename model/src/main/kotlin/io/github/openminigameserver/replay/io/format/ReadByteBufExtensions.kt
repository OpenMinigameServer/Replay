package io.github.openminigameserver.replay.io.format

import com.esotericsoftware.kryo.io.Input
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.ReplayHeader
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration
import kotlin.time.milliseconds

fun Input.readUUID(): UUID {
    return UUID(readLong(), readLong())
}

fun Input.readInstant(): Instant {
    return Instant.fromEpochSeconds(readLong())
}

fun Input.readObject(): Any {
    return kryo.readClassAndObject(this)
}

fun Input.readMap(): Map<out Any, Any> {
    val result = mutableMapOf<Any, Any>()
    repeat(readInt()) {
        result[readObject()] = readObject()
    }
    return result
}

fun Input.readDuration(): Duration {
    return readLong().milliseconds
}

fun Input.readHeader(destination: ReplayHeader) {
    destination.apply {
        version = readInt()
        id = readUUID()
        recordStartTime = readInstant()
        metadata = readMap() as MutableMap<String, Any>
        duration = readDuration()
    }
}


fun Input.readReplayData(replay: Replay) {
    replay.apply {
        repeat(readInt()) {
            entities[readInt()] = readObject() as RecordableEntity
        }

        repeat(readInt()) {
            (readObject() as? RecordableAction?).also { actions.add(it) }
        }
    }
}