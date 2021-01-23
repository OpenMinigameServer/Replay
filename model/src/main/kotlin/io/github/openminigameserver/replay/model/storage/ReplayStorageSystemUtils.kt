package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.Replay
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.CompletableFuture

object ReplayStorageSystemUtils {
    @JvmStatic
    fun ReplayStorageSystem.getReplaysForPlayerAsCompletable(player: UUID): CompletableFuture<List<UUID>> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync runBlocking { getReplaysForPlayer(player) }
        }
    }

    @JvmStatic
    fun ReplayStorageSystem.loadReplayAsCompletable(uuid: UUID): CompletableFuture<Replay?> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync runBlocking { loadReplay(uuid) }
        }
    }

    @JvmStatic
    fun ReplayStorageSystem.saveReplayAsCompletable(replay: Replay): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            return@runAsync runBlocking { saveReplay(replay) }
        }
    }
}