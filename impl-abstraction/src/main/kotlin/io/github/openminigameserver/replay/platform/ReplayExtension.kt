package io.github.openminigameserver.replay.platform

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.commands.ReplayCommand
import io.github.openminigameserver.replay.commands.StartRecordingCommand
import io.github.openminigameserver.replay.commands.StopRecordingCommand
import kotlinx.coroutines.runBlocking

class ReplayExtension(var platform: ReplayPlatform<out ReplayWorld, out ReplayUser, out ReplayEntity>) {

    val dataDir get() = platform.dataDir

    val replayManager = ReplayManager(this)

    fun init() {
        platform.log("Replay by OpenMinigameServer version ${io.github.openminigameserver.replay.BuildInfo.version}.")
        prepareCommandManager()

        if (platform.settings.shouldRegisterCommands) {
            platform.commandAnnotationParser.parse(ReplayCommand)
            platform.commandAnnotationParser.parse(StartRecordingCommand)
            platform.commandAnnotationParser.parse(StopRecordingCommand)
        }
    }

    private fun prepareCommandManager() {
        platform.commandManager.parameterInjectorRegistry().apply {
            registerInjector(ReplayExtension::class.java) { _, _ -> this@ReplayExtension }
            registerInjector(ReplayManager::class.java) { _, _ -> replayManager }
        }

        platform.commandManager.parserRegistry.registerSuggestionProvider("replay") { t, _ ->
            runBlocking { replayManager.storageSystem.getReplaysForPlayer(t.sender.uuid) }.map { it.toString() }
        }
    }
}