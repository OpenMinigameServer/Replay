package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.ReplayFile
import net.minestom.server.entity.Player
import java.util.*

interface ReplayStorageSystem {

    suspend fun getReplaysForPlayer(player: Player): List<UUID>

    suspend fun loadReplay(uuid: UUID): ReplayFile?

    suspend fun saveReplay(replay: ReplayFile)

}