package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.ReplayFile
import java.util.*

interface ReplayStorageSystem {

    suspend fun getReplaysForPlayer(player: UUID): List<UUID>

    suspend fun loadReplay(uuid: UUID): ReplayFile?

    suspend fun saveReplay(replay: ReplayFile)

}