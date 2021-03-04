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
import io.github.openminigameserver.replay.replayer.ActionPlayerManager
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import java.io.File
import java.util.*

abstract class ReplayPlatform<W : ReplayWorld, P : ReplayUser, E : ReplayEntity> {
    abstract val worlds: IdHelperContainer<UUID, W>
    abstract val entities: IdHelperContainer<UUID, ReplayEntity>

    abstract val name: String
    abstract val version: String

    abstract val commandManager: CommandManager<ReplayUser>
    abstract val commandAnnotationParser: AnnotationParser<ReplayUser>

    abstract val actionPlayerManager: ActionPlayerManager<W, P, E>

    abstract val dataDir: File

    abstract fun log(message: String)

    val settings = ReplayExtensionSettings()

    abstract fun registerSyncRepeatingTask(time: TickTime, action: () -> Unit): Any

    abstract fun cancelTask(tickerTask: Any)

    abstract fun getEntityType(replayEntity: E): String
    abstract fun getEntityData(replayEntity: E): BaseEntityData?

    abstract fun addToViewerTeam(p: P)
    abstract fun removeFromViewerTeam(player: P)

    abstract fun getWorldById(it: UUID): W
    abstract fun unregisterWorld(instance: W)

    abstract fun getEntityManager(replaySession: ReplaySession): IEntityManager<P, E>

    abstract fun createReplaySession(
        replay: Replay,
        viewers: MutableList<ReplayUser>,
        instance: ReplayWorld? = null,
        tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
    ): ReplaySession

    abstract fun getPlayerInventoryCopy(player: P): Any

    abstract fun loadPlayerInventoryCopy(player: P, inventory: Any)
}