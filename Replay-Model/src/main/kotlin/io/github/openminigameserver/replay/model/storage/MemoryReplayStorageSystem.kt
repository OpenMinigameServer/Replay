package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.ReplayFile
import java.util.*

object MemoryReplayStorageSystem : ReplayStorageSystem {
    private val replays = mutableMapOf<UUID, ReplayFile>()

    override suspend fun getReplaysForPlayer(player: UUID): List<UUID> {
        return replays.keys.toList()
    }

    override suspend fun loadReplay(uuid: UUID): ReplayFile? {
        return replays[uuid]
    }

    override suspend fun saveReplay(replay: ReplayFile) {
        replays[replay.id] = replay
    }
}