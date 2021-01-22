/*
package io.github.openminigameserver.replay.model.storage

import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.openminigameserver.replay.ReplayExtension.Companion.dataFolder
import io.github.openminigameserver.replay.model.ReplayFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.minestom.server.entity.Player
import java.io.File
import java.util.*

object FileReplayStorageSystem : ReplayStorageSystem {
    private val mapper = SmileMapper().apply {
        registerKotlinModule()
        registerModule(Jdk8Module())
        registerModule(JavaTimeModule())
    }

    private val replaysFolder = File(dataFolder, "replays").also { it.mkdirs() }

    override suspend fun getReplaysForPlayer(player: Player): List<UUID> {
        return replaysFolder.listFiles()
            ?.mapNotNull { kotlin.runCatching { UUID.fromString(it.nameWithoutExtension) }.getOrNull() } ?: emptyList()
    }

    override suspend fun loadReplay(uuid: UUID): ReplayFile? {
        return File(replaysFolder, "$uuid.replay").takeIf { it.exists() }?.let {
            withContext(Dispatchers.IO) {
                mapper.readValue(it, ReplayFile::class.java)
            }
        }
    }

    override suspend fun saveReplay(replay: ReplayFile) {
        val targetFile = File(replaysFolder, "${replay.id}.replay")

        withContext(Dispatchers.IO) {
            val result = mapper.writeValueAsBytes(replay)
            targetFile.writeBytes(result)
        }

    }
}*/
