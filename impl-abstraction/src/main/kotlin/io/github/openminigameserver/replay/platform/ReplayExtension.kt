package io.github.openminigameserver.replay.platform

import io.github.openminigameserver.replay.BuildInfo
import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.commands.ReplayCommand
import kotlinx.coroutines.runBlocking

class ReplayExtension(var platform: ReplayPlatform) {

    val dataDir get() = platform.dataDir

    val replayManager = ReplayManager(this)

    fun init() {
        platform.log("Replay by OpenMinigameServer version ${BuildInfo.version}.")
        prepareCommandManager()

        platform.commandAnnotationParser.parse(ReplayCommand)
    }

    private fun prepareCommandManager() {
        platform.commandManager.parameterInjectorRegistry()
            .registerInjector(ReplayManager::class.java) { _, _ ->
                replayManager
            }

        platform.commandManager.parserRegistry.registerSuggestionProvider("replay") { t, _ ->
            runBlocking { replayManager.storageSystem.getReplaysForPlayer(t.sender.uuid) }.map { it.toString() }
        }
    }
}