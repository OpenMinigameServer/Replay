package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.replay.MinestomReplayExtension
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.abstraction.*
import io.github.openminigameserver.replay.helpers.EntityManager
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.platform.IdHelperContainer
import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.platform.minestom.replayer.getPlayerInventoryCopy
import io.github.openminigameserver.replay.platform.minestom.replayer.loadAllItems
import io.github.openminigameserver.replay.replayer.ActionPlayerManager
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplayChunkLoader
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.metadata.PlayerHeadMeta
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
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
        return replayEntity.entity.entityType.namespaceID
    }

    override fun getEntityData(replayEntity: MinestomReplayEntity): BaseEntityData? {
        return null
    }

    override val actionPlayerManager: ActionPlayerManager<MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity> =
        MinestomActionPlayerManager(this)

    private val viewerTeam: Team = MinecraftServer.getTeamManager().createBuilder("ReplayViewers")
        .prefix(ColoredText.of(ChatColor.GRAY, "[Viewer] "))
        .collisionRule(TeamsPacket.CollisionRule.NEVER)
        .teamColor(ChatColor.GRAY)
        .build()

    override fun addToViewerTeam(p: MinestomReplayUser) {
        viewerTeam.addMember(p.player.username)
    }

    override fun removeFromViewerTeam(player: MinestomReplayUser) {
        viewerTeam.removeMember(player.player.username)
    }

    override fun unregisterWorld(instance: MinestomReplayWorld) {
        MinecraftServer.getInstanceManager().unregisterInstance(instance.instance)
    }

    override fun getWorldById(it: UUID): MinestomReplayWorld {
        return worlds.getOrCompute(it)
    }

    override fun getEntityManager(replaySession: ReplaySession): IEntityManager<MinestomReplayUser, MinestomReplayEntity> {
        return EntityManager(MinestomReplayExtension.platform, replaySession)
    }

    private fun createEmptyReplayInstance(replay: Replay) =
        (
                MinecraftServer.getInstanceManager().createInstanceContainer().apply {
                    enableAutoChunkLoad(true)
                    data = DataImpl()
                    chunkLoader = ReplayChunkLoader(replay)
                }
                ).let { worlds.getOrCompute(it.uniqueId) }

    override fun createReplaySession(
        replay: Replay,
        viewers: MutableList<ReplayUser>,
        instance: ReplayWorld?,
        tickTime: TickTime
    ): ReplaySession {
        val hasChunks = replay.hasChunks
        val finalInstance =
            if (hasChunks) createEmptyReplayInstance(replay) else instance!!

        return ReplaySession(
            MinestomReplayExtension.platform as ReplayPlatform<ReplayWorld, ReplayUser, ReplayEntity>,
            finalInstance,
            replay,
            viewers,
            tickTime
        )
    }

    override fun getPlayerInventoryCopy(player: MinestomReplayUser): Any {
        return getPlayerInventoryCopy(player.player)
    }

    override fun loadPlayerInventoryCopy(player: MinestomReplayUser, inventory: Any) {
        @Suppress("UNCHECKED_CAST")
        (inventory as? NBTList<NBTCompound>)?.let { loadAllItems(it, player.player.inventory) }
    }

    fun getPlayer(player: Player): ReplayUser {
        return entities.getOrCompute(player.entityId) as ReplayUser
    }

    fun getItemStack(itemInHand: ItemStack): ReplayActionItemStack {
        val action = itemInHand.controlItemAction
        val skin = (itemInHand.itemMeta as? PlayerHeadMeta)?.playerSkin?.let {
            ReplayHeadTextureSkin(
                it.textures,
                it.signature
            )
        }

        return ReplayActionItemStack(itemInHand.displayName?.let {
            MinestomComponentSerializer.get().deserialize(
                it
            )
        } ?: Component.empty(), action, skin)
    }

    fun getEntity(entity: Entity): ReplayEntity {
        return entities.getOrCompute(entity.entityId)
    }

    override val worlds: IdHelperContainer<UUID, MinestomReplayWorld> =
        IdHelperContainer { MinestomReplayWorld(MinecraftServer.getInstanceManager().getInstance(this)!!) }

    override val entities: IdHelperContainer<Int, ReplayEntity>
        get() = IdHelperContainer {
            val entity: Entity = Entity.getEntity(this) ?: Player.getEntity(this)!!
            if (entity is Player) MinestomReplayUser(this@MinestomReplayPlatform, entity) else
                MinestomReplayEntity(this@MinestomReplayPlatform, entity)
        }
}