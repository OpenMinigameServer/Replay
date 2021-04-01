package io.github.openminigameserver.replay.platform.bukkit

import com.comphenix.protocol.wrappers.AdventureComponentConverter
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedGameProfile
import io.github.openminigameserver.replay.PlayerNameSplittingHelper
import io.github.openminigameserver.replay.ReplayPlugin
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.platform.bukkit.packets.WrapperPlayServerPlayerInfo
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import io.github.openminigameserver.replay.runSync
import io.github.openminigameserver.replay.toBukkit
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.citizensnpcs.npc.ai.NPCHolder
import net.citizensnpcs.npc.skin.SkinnableEntity
import net.citizensnpcs.trait.Gravity
import net.citizensnpcs.trait.SkinTrait
import net.citizensnpcs.util.NMS
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class BukkitEntityManager(val platform: BukkitReplayPlatform, override var session: ReplaySession) :
    IEntityManager<BukkitReplayUser, BukkitReplayEntity> {
    private val npcRegistry = CitizensAPI.createAnonymousNPCRegistry(MemoryNPCDataStore())

    private val world get() = (session.world as BukkitReplayWorld).world

    override val entities: Collection<RecordableEntity>
        get() = replayEntities.keys.mapNotNull { session.replay.getEntityById(it) }

    override val replayEntities: MutableMap<Int, BukkitReplayEntity> = mutableMapOf()

    override fun getNativeEntity(entity: RecordableEntity): BukkitReplayEntity? {
        return replayEntities[entity.id]
    }

    override fun spawnEntity(entity: RecordableEntity, position: RecordablePosition, velocity: RecordableVector) {
        runSync {
            val type = NamespacedKey.fromString(entity.type)?.let { Registry.ENTITY_TYPE.get(it) } ?: TODO(entity.type)
            val entityData = entity.entityData
            if (type == EntityType.PLAYER && entityData is PlayerEntityData) {
                val nativeEntity = getNativeEntity(entity)
                val name = PlayerNameSplittingHelper.splitName(entityData.userName)
                val npc = nativeEntity?.takeIf { it.entity is NPCHolder }?.let { (it.entity as NPCHolder).npc }
                    ?: npcRegistry.createNPC(type, (name.name))

                npc.getOrAddTrait(Gravity::class.java).gravitate(true)
                npc.data().set("removefromtablist", false)
                npc.spawn(position.toBukkit(world))

                //Add NPC to tablist
                val npcEntity = npc.entity
                if (npcEntity is SkinnableEntity) {
                    sendNpcTabEntryAction(npcEntity as Player, EnumWrappers.PlayerInfoAction.ADD_PLAYER)
                }

                if (nativeEntity != null) {
                    refreshPosition(nativeEntity, position)
                }

                val skinData = entityData.skin
                if (skinData != null) {
                    val skinTrait = npc.getOrAddTrait(SkinTrait::class.java)
                    skinTrait.setSkinPersistent(
                        entityData.userName,
                        Base64.getEncoder().encodeToString(skinData.signature),
                        Base64.getEncoder().encodeToString(skinData.textures)
                    )
                }

                saveReplayEntity(entity, npcEntity)
                platform.setSplitPlayerName(session, getNativeEntity(entity)!!, name.rest)
                return@runSync
            }
            entityData ?: return@runSync

            TODO("Entities $type $entityData")
        }
    }


    private fun saveReplayEntity(rec: RecordableEntity, entity: Entity) {
        replayEntities[rec.id] = platform.entities.getOrCompute(entity.entityId) as BukkitReplayEntity
    }

    override fun refreshPosition(entity: BukkitReplayEntity, position: RecordablePosition) {
        runSync {
            val toBukkit = position.toBukkit(world)
            val nativeEntity = entity.entity
            if (nativeEntity is NPCHolder) {
                nativeEntity.npc.teleport(toBukkit, PlayerTeleportEvent.TeleportCause.COMMAND)
                NMS.look(nativeEntity, toBukkit.yaw, toBukkit.pitch)
            } else {
                nativeEntity.teleport(toBukkit, PlayerTeleportEvent.TeleportCause.COMMAND)
            }
        }
    }

    override fun removeNativeEntity(entity: BukkitReplayEntity, destroy: Boolean) = runSync {
        val native = entity.entity
        if (native is NPCHolder) {
            val npc = native.npc

            if (destroy) {
                npc.destroy()
            } else {
                npc.despawn()
            }

            if (native is Player) {
                //Remove NPC from tablist
                sendNpcTabEntryAction(npc.entity as Player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)
            }
        } else {
            native.remove()
        }
        replayEntities.remove(entity.id)
    }

    private fun sendNpcTabEntryAction(
        player: Player,
        action: EnumWrappers.PlayerInfoAction
    ) {
        Bukkit.getScheduler().runTaskLater(ReplayPlugin.instance, Runnable {
            WrapperPlayServerPlayerInfo().apply {
                this.action = action
                this.data = mutableListOf(
                    PlayerInfoData(
                        WrappedGameProfile.fromPlayer(player),
                        0,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        if (action == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER) AdventureComponentConverter.fromComponent(
                            text(ReplayListener.removeNpcTagName)
                        ) else null
                    )
                )
            }.broadcastPacket()
        }, 1L)
    }

    override fun removeEntity(entity: RecordableEntity, destroy: Boolean) {
        replayEntities[entity.id]?.let { removeNativeEntity(it, destroy) }
    }

    override fun removeEntityViewer(player: BukkitReplayUser) {

    }

    override fun removeAllEntities() {
        entities.forEach { removeEntity(it) }
    }
}