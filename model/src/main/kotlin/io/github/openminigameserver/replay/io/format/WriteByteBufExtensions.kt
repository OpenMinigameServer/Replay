package io.github.openminigameserver.replay.io.format

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.ReplayHeader
import kotlinx.datetime.Instant
import org.objenesis.strategy.StdInstantiatorStrategy
import java.util.*
import kotlin.time.Duration


var kryo = Kryo().apply {
    isRegistrationRequired = false
    instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
}

fun Output.writeUUID(id: UUID) {
    writeLong(id.mostSignificantBits)
    writeLong(id.leastSignificantBits)
}

fun Output.writeInstant(instant: Instant) {
    writeLong(instant.epochSeconds)
}

fun Output.writeDuration(duration: Duration) {
    writeLong(duration.toLongMilliseconds())
}

fun Output.writeObject(obj: Any) {
    kryo.writeClassAndObject(this, obj)
}

inline fun <reified C> Output.writeCollection(obj: Collection<C>) {
    writeInt(obj.size)
    obj.forEach { kryo.writeObjectOrNull(this, it, C::class.java) }
}

fun Output.writeMap(map: Map<out Any, Any>) {
    writeInt(map.size)
    map.forEach { (k, v) ->
        writeObject(k)
        writeObject(v)
    }
}

fun Output.writeHeader(header: ReplayHeader) {
    header.apply {
        writeInt(version)
        writeUUID(id)
        writeInstant(recordStartTime)
        writeMap(metadata)
        writeDuration(duration)
        if (version > 3) {
            writeCollection(chunks)
        }
    }
}

fun Output.writeReplayData(replay: Replay) {
    replay.apply {
        writeInt(entities.size)
        entities.forEach { (id, entity) ->
            writeInt(id)
            writeObject(entity)
        }

        writeInt(replay.actions.size)
        replay.actions.forEach {
            writeObject(it)
        }
    }
}

