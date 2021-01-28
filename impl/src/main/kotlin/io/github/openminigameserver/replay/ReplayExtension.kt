package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.commands.ReplayCommand
import io.github.openminigameserver.replay.commands.ReplayCommandManager
import io.github.openminigameserver.replay.commands.StartRecordingCommand
import io.github.openminigameserver.replay.commands.StopRecordingCommand
import io.github.openminigameserver.replay.helpers.EntityHelper
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
            classLoader.protectedClasses.add("io.github.openminigameserver.replay.AbstractReplaySession")
            classLoader.protectedPackages.add("io.github.openminigameserver.replay.model")
        }
        logger.info("Replay by OpenMinigameServer version ${BuildInfo.version}.")


        ReplayCommandManager().apply {
            annotationParser.parse(StartRecordingCommand)
            annotationParser.parse(StopRecordingCommand)
            annotationParser.parse(ReplayCommand)

        }

        ReplayListener.registerListeners()
        logger.info("Initialized event listeners.")

        EntityHelper.init()
        logger.info("Initialized entity helpers.")
    }

    override fun terminate() {

    }
}