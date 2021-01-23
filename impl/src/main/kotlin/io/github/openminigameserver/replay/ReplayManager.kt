package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.storage.MemoryReplayStorageSystem
import io.github.openminigameserver.replay.model.storage.ReplayStorageSystem

object ReplayManager {
    val storageSystem: ReplayStorageSystem = MemoryReplayStorageSystem

    fun createEmptyReplay() = Replay()
}