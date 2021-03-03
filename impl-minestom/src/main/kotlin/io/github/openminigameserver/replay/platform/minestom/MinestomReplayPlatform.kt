package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.MinestomReplayExtension
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.platform.ReplayPlatform
import net.minestom.server.MinecraftServer
import java.io.File

class MinestomReplayPlatform(private val replayExtension: MinestomReplayExtension) : ReplayPlatform() {
    override val name: String
        get() = "Minestom"
    override val version: String
        get() = MinecraftServer.VERSION_NAME

    override val commandManager: CommandManager<ReplayUser> = ReplayCommandManager()
    override val commandAnnotationParser: AnnotationParser<ReplayUser> =
        AnnotationParser(commandManager, ReplayUser::class.java) { commandManager.createDefaultCommandMeta() }
    override val dataDir: File
        get() = MinestomReplayExtension.dataFolder

    override fun log(message: String) {
        replayExtension.minestomLogger.info(message)
    }
}