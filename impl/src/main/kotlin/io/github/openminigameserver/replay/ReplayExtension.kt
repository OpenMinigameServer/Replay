package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.commands.ReplayCommand
import io.github.openminigameserver.replay.commands.StartRecordingCommand
import io.github.openminigameserver.replay.commands.StopRecordingCommand
import io.github.openminigameserver.replay.io.ReplayFile
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import net.minestom.server.extras.selfmodification.MinestomRootClassLoader
import java.io.File

class ReplayExtension : Extension() {
    companion object {
        @JvmStatic
        val dataFolder by lazy {
            File(
                MinecraftServer.getExtensionManager().extensionFolder,
                "Replay"
            ).also { it.mkdirs() }
        }
    }

    override fun initialize() {
        val classLoader = this.javaClass.classLoader
        if (classLoader is MinestomRootClassLoader) {
            classLoader.protectedPackages.add("io.github.openminigameserver.replay.model")
        }
        logger.info("Replay by OpenMinigameServer version ${BuildInfo.version}.")

        MinecraftServer.getCommandManager().apply {
            register(StartRecordingCommand)
            register(StopRecordingCommand)
            register(ReplayCommand)
        }

        ReplayListener.registerListeners()
        ReplayFile.doMapAttempt()
    }

    override fun terminate() {

    }
}