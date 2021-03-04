package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.helpers.EntityHelper
import io.github.openminigameserver.replay.platform.ReplayExtension
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayPlatform
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import net.minestom.server.extras.selfmodification.MinestomRootClassLoader
import org.slf4j.Logger
import java.io.File

class MinestomReplayExtension : Extension() {
    val minestomLogger: Logger get() = this.logger

    companion object {
        @JvmStatic
        lateinit var dataFolder: File

        lateinit var platform: MinestomReplayPlatform

        lateinit var extension: ReplayExtension
    }

    init {
        val classLoader = this.javaClass.classLoader
        if (classLoader is MinestomRootClassLoader) {
            classLoader.protectedClasses.add("io.github.openminigameserver.replay.AbstractReplaySession")
            classLoader.protectedPackages.add("io.github.openminigameserver.replay.model")
        }
    }

    override fun initialize() {
        dataFolder = File(
            MinecraftServer.getExtensionManager().extensionFolder,
            "Replay"
        ).also { it.mkdirs() }
        platform = MinestomReplayPlatform(this)
        extension = ReplayExtension(platform)

        extension.init()

        ReplayListener.platform = platform
        ReplayListener.registerListeners()
        logger.info("Initialized event listeners.")

        EntityHelper.init()
        logger.info("Initialized entity helpers.")
    }

    override fun terminate() {

    }
}