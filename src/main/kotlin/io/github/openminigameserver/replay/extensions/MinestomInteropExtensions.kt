package io.github.openminigameserver.replay.extensions

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.player.ReplaySession
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import java.util.*
import java.util.concurrent.TimeUnit

fun Position.toReplay(): RecordablePosition = RecordablePosition(x, y, z, yaw, pitch)
fun RecordablePosition.toMinestom(): Position = Position(x, y, z, yaw, pitch)

const val REPLAY_RECORDER_DATA = "replay:recorder"
const val REPLAY_REPLAYER_DATA = "replay:replayer"

var Player.recorder: ReplayRecorder?
    get() = instance!!.recorder
    set(value) {
        instance!!.recorder = value
    }

var Instance.recorder: ReplayRecorder?
    get() = data?.get(REPLAY_RECORDER_DATA)
    set(value) {
        data?.set(REPLAY_RECORDER_DATA, value)
    }

var Instance.replaySession: ReplaySession?
    get() = data?.get(REPLAY_REPLAYER_DATA)
    set(value) {
        data?.set(REPLAY_REPLAYER_DATA, value)
    }

internal fun runOnSeparateThread(code: suspend CoroutineScope.() -> Unit) {
    Thread {
        runBlocking(block = code)
    }.start()
}

val profileCache: Cache<UUID, PlayerSkin> =
    CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build()

fun Entity.toReplay(): RecordableEntity {
    var data: Any? = null
    if (this is Player) {
        val skin =
            skin ?: profileCache.get(uuid) { kotlin.runCatching { PlayerSkin.fromUuid(uuid.toString()) }.getOrNull() }

        data =
            PlayerEntityData(username, skin)
    }
    return RecordableEntity(entityId, entityType.namespaceID, position.toReplay(), data)
}