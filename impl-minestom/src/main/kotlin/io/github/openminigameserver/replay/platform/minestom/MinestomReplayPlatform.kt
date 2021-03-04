package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.MinestomReplayExtension
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.abstraction.ReplayActionItemStack
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.platform.IdHelperContainer
import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.replayer.ActionPlayerManager
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.io.File
import java.util.*

class MinestomReplayPlatform(private val replayExtension: MinestomReplayExtension) :
    ReplayPlatform<MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity>() {
    override val name: String
        get() = "Minestom"
    override val version: String
        get() = MinecraftServer.VERSION_NAME

    override val commandManager: CommandManager<ReplayUser> = ReplayCommandManager(this)
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

    override fun getEntityType(replayEntity: MinestomReplayEntity): String {
        TODO("Not yet implemented")
    }

    override fun getEntityData(replayEntity: MinestomReplayEntity): BaseEntityData? {
        TODO("Not yet implemented")
    }

    override val actionPlayerManager: ActionPlayerManager<MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity> =
        MinestomActionPlayerManager(this)

    override fun addToViewerTeam(p: MinestomReplayUser) {
        TODO("Not yet implemented")
    }

    override fun unregisterWorld(instance: MinestomReplayWorld) {
        MinecraftServer.getInstanceManager().unregisterInstance(instance.instance)
    }

    override fun removeFromViewerTeam(player: MinestomReplayUser) {
        TODO("Not yet implemented")
    }

    override fun getWorldById(it: UUID): MinestomReplayWorld {
        TODO("Not yet implemented")
    }

    override fun getEntityManager(replaySession: ReplaySession): IEntityManager<MinestomReplayUser, MinestomReplayEntity> {
        TODO("Not yet implemented")
    }

    override fun createReplaySession(
        replay: Replay,
        viewers: MutableList<ReplayUser>,
        instance: ReplayWorld?,
        tickTime: TickTime
    ): ReplaySession {
        TODO("Not yet implemented")
    }

    override fun getPlayerInventoryCopy(player: MinestomReplayUser): Any {
        TODO("Not yet implemented")
    }

    override fun loadPlayerInventoryCopy(player: MinestomReplayUser, inventory: Any) {
        TODO("Not yet implemented")
    }

    fun getPlayer(player: Player): ReplayUser {
        return entities.getOrCompute(player.uuid) as ReplayUser
    }

    fun getItemStack(itemInHand: ItemStack): ReplayActionItemStack {
        TODO("Not yet implemented")
    }

    override val worlds: IdHelperContainer<UUID, MinestomReplayWorld> =
        IdHelperContainer { MinestomReplayWorld(MinecraftServer.getInstanceManager().getInstance(this)!!) }
    override val entities: IdHelperContainer<UUID, ReplayEntity>
        get() = IdHelperContainer {
            val entity = Entity.getEntity(this)!!
            if (entity is Player) MinestomReplayUser(this@MinestomReplayPlatform, entity) else
                MinestomReplayEntity(this@MinestomReplayPlatform, entity)
        }
}