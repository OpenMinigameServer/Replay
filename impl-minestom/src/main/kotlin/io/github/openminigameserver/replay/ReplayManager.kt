package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.storage.FileReplayStorageSystem
import io.github.openminigameserver.replay.model.storage.ReplayStorageSystem

object ReplayManager {
    @JvmStatic
    var storageSystem: ReplayStorageSystem = FileReplayStorageSystem(MinestomReplayExtension.dataFolder)

    @JvmStatic
    fun createEmptyReplay() = Replay()
}