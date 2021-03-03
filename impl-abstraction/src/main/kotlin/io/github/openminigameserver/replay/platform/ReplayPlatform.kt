package io.github.openminigameserver.replay.platform

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import java.io.File

abstract class ReplayPlatform {
    abstract val name: String
    abstract val version: String

    abstract val commandManager: CommandManager<ReplayUser>
    abstract val commandAnnotationParser: AnnotationParser<ReplayUser>

    abstract val dataDir: File

    abstract fun log(message: String)

    val settings = ReplayExtensionSettings()

    abstract fun cancelTask(tickerTask: Any)

    abstract fun registerSyncRepeatingTask(time: TickTime, action: () -> Unit): Any

    abstract fun getEntityType(replayEntity: ReplayEntity): String
    abstract fun getEntityData(replayEntity: ReplayEntity): BaseEntityData?
}