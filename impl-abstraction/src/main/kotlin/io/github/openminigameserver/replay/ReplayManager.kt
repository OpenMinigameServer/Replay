package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.storage.FileReplayStorageSystem
import io.github.openminigameserver.replay.model.storage.ReplayStorageSystem
import io.github.openminigameserver.replay.platform.ReplayExtension

class ReplayManager(extension: ReplayExtension) {
    var storageSystem: ReplayStorageSystem = FileReplayStorageSystem(extension.dataDir)

    fun createEmptyReplay() = Replay()
}