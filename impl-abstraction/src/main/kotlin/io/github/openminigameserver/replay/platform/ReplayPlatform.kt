package io.github.openminigameserver.replay.platform

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.TimeUnit
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import java.io.File
import java.util.*

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
    abstract fun addToViewerTeam(p: ReplayUser)
    abstract fun unregisterWorld(instance: ReplayWorld)
    abstract fun removeFromViewerTeam(player: ReplayUser)
    abstract fun getWorldById(it: UUID): ReplayWorld
    abstract fun getEntityManager(replaySession: ReplaySession): IEntityManager<*, *>

    abstract fun createReplaySession(
        replay: Replay,
        viewers: MutableList<ReplayUser>,
        instance: ReplayWorld? = null,
        tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
    ): ReplaySession
}