package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.Replay
import java.util.*

interface ReplayStorageSystem {

    suspend fun getReplaysForPlayer(player: UUID): List<UUID>

    suspend fun loadReplay(uuid: UUID): Replay?

    suspend fun saveReplay(replay: Replay)

}