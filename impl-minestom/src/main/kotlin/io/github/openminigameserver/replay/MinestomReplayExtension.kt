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
        val dataFolder by lazy {
            File(
                MinecraftServer.getExtensionManager().extensionFolder,
                "Replay"
            ).also { it.mkdirs() }
        }
    }

    init {
        val classLoader = this.javaClass.classLoader
        if (classLoader is MinestomRootClassLoader) {
            classLoader.protectedClasses.add("io.github.openminigameserver.replay.AbstractReplaySession")
            classLoader.protectedPackages.add("io.github.openminigameserver.replay.model")
        }
    }

    val platform = MinestomReplayPlatform(this)
    val extension = ReplayExtension(platform)

    override fun initialize() {
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