package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.MinestomReplayExtension
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.platform.ReplayPlatform
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
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

    override fun cancelTask(tickerTask: Any) {
        val task = tickerTask as? Task ?: return
        task.cancel()
    }

    override fun registerSyncRepeatingTask(time: TickTime, action: () -> Unit): Any {
        return MinecraftServer.getSchedulerManager().buildTask(action)
            .repeat(time.unit.toMilliseconds(time.time), TimeUnit.MILLISECOND).schedule()
    }

    override fun getEntityType(replayEntity: ReplayEntity): String {
        TODO("Not yet implemented")
    }

    override fun getEntityData(replayEntity: ReplayEntity): BaseEntityData? {
        TODO("Not yet implemented")
    }
}