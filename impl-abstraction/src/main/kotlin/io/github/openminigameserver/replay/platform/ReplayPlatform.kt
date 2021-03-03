package io.github.openminigameserver.replay.platform

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.ReplayUser
import java.io.File

abstract class ReplayPlatform {
    abstract val name: String
    abstract val version: String

    abstract val commandManager: CommandManager<ReplayUser>
    abstract val commandAnnotationParser: AnnotationParser<ReplayUser>

    abstract val dataDir: File

    abstract fun log(message: String)

    val settings = ReplayExtensionSettings()
}