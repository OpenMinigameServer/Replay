package io.github.openminigameserver.replay.model.storage

import io.github.openminigameserver.replay.model.Replay
import kotlinx.coroutines.future.await
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class CompletableFutureReplayStorageSystem : ReplayStorageSystem {

    abstract fun getReplaysForPlayerCompletable(player: UUID): CompletableFuture<List<UUID>>
    abstract fun loadReplayCompletable(uuid: UUID): CompletableFuture<Replay?>
    abstract fun saveReplayCompletable(replay: Replay): CompletableFuture<Void>

    override suspend fun getReplaysForPlayer(player: UUID): List<UUID> {
        return getReplaysForPlayerCompletable(player).await()    }

    override suspend fun loadReplay(uuid: UUID): Replay? {
        return loadReplayCompletable(uuid).await()
    }

    override suspend fun saveReplay(replay: Replay) {
        saveReplayCompletable(replay).await()
    }
}