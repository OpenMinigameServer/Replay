package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.commands.ReplayCommand
import io.github.openminigameserver.replay.commands.StartRecordingCommand
import io.github.openminigameserver.replay.commands.StopRecordingCommand
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import java.io.File

class ReplayExtension : Extension() {
    companion object {
        val dataFolder by lazy { File(MinecraftServer.getExtensionManager().extensionFolder, "Replay").also { it.mkdirs() } }
    }

    override fun initialize() {
        logger.info("Replay by OpenMinigameServer version ${BuildInfo.version}.")

        MinecraftServer.getCommandManager().apply {
            register(StartRecordingCommand)
            register(StopRecordingCommand)
            register(ReplayCommand)
        }

        ReplayListener.registerListener()
    }

    override fun terminate() {

    }
}