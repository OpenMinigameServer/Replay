package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.io.ReplayFile
import io.github.openminigameserver.replay.model.Replay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class FileReplayStorageSystem(dataFolder: File) : ReplayStorageSystem {

    private val replaysFolder = File(dataFolder, "replays").also { it.mkdirs() }

    override suspend fun getReplaysForPlayer(player: UUID): List<UUID> {
        return replaysFolder.listFiles()
            ?.mapNotNull { kotlin.runCatching { UUID.fromString(it.nameWithoutExtension) }.getOrNull() } ?: emptyList()
    }

    override suspend fun loadReplay(uuid: UUID): Replay? {
        return File(replaysFolder, "$uuid.$replayExtension").takeIf { it.exists() }?.let {
            withContext(Dispatchers.IO) {
                ReplayFile(it).let { it.loadReplay(); it.replay }
            }
        }
    }

    override suspend fun saveReplay(replay: Replay) {
        val targetFile = File(replaysFolder, "${replay.id}.$replayExtension")

        withContext(Dispatchers.IO) {
            ReplayFile(targetFile, replay).also { it.saveReplay() }
        }


    }

    companion object {
        const val replayExtension = "osmrp"
    }
}
