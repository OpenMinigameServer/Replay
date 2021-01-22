package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.Replay
import java.util.*

object MemoryReplayStorageSystem : ReplayStorageSystem {
    private val replays = mutableMapOf<UUID, Replay>()

    override suspend fun getReplaysForPlayer(player: UUID): List<UUID> {
        return replays.keys.toList()
    }

    override suspend fun loadReplay(uuid: UUID): Replay? {
        return replays[uuid]
    }

    override suspend fun saveReplay(replay: Replay) {
        replays[replay.id] = replay
    }
}