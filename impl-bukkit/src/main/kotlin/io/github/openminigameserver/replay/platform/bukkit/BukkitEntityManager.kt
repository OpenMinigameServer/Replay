package io.github.openminigameserver.replay.platform.bukkit

import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import io.github.openminigameserver.replay.toBukkit
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerTeleportEvent
import kotlin.time.Duration

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
        val type = NamespacedKey.fromString(entity.type)?.let { Registry.ENTITY_TYPE.get(it) } ?: TODO(entity.type)
        val entityData = entity.entityData
        if (type == EntityType.PLAYER && entityData is PlayerEntityData) {
            val npc = npcRegistry.createNPC(type, (entityData.userName + "§r"))
            npc.spawn(position.toBukkit(world))
            saveReplayEntity(entity, npc.entity)
            return
        }
        entityData ?: return

        TODO("Entities $type $entityData")
    }


    private fun saveReplayEntity(rec: RecordableEntity, entity: Entity) {
        replayEntities[rec.id] = platform.entities.getOrCompute(entity.entityId) as BukkitReplayEntity
    }

    override fun refreshPosition(minestomEntity: BukkitReplayEntity, position: RecordablePosition) {
        minestomEntity.entity.teleport(position.toBukkit(world), PlayerTeleportEvent.TeleportCause.COMMAND)
    }

    override fun removeNativeEntity(entity: BukkitReplayEntity) {
        entity.entity.remove()
        replayEntities.remove(entity.id)
    }

    override fun removeEntity(entity: RecordableEntity) {
        replayEntities[entity.id]?.let { removeNativeEntity(it) }
    }

    override fun removeEntityViewer(player: BukkitReplayUser) {

    }

    override fun removeAllEntities() {
        entities.forEach { removeEntity(it) }
    }

    override fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration) {
        TODO("Not yet implemented")
    }
}