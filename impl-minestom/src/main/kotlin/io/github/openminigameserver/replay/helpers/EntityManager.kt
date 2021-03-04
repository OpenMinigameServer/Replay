/*
package io.github.openminigameserver.replay.helpers

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayUser
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.registry.Registries
import net.minestom.server.utils.Position
import net.minestom.server.utils.Vector
import kotlin.time.Duration

class EntityManager(override var session: ReplaySession) : IEntityManager<MinestomReplayUser, TODO()> {
    override val entities: Collection<RecordableEntity>
        get() = replayEntities.keys.mapNotNull { session.replay.getEntityById(it) }

    //Replay entity id
    override val replayEntities = mutableMapOf<Int, Entity>()

    override fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration) {
        getNativeEntity(entity)?.let { nativeEntity ->
            nativeEntity.remove()
            nativeEntity.askSynchronization()

            //Check if Entity (has been spawned at start) or (has been spawned somewhere before and has not been removed before)
            val shouldSpawn = true

            //Find actual position
            var finalPos = entity.spawnPosition?.position
            session.findActionsForEntity<RecEntityMove>(startTime, entity, targetReplayTime)
                ?.let { finalPos = it.data.position }
            session.findLastAction<RecEntitiesPosition>(
                startTime,
                targetReplayTime
            ) { it.positions.containsKey(entity) }
                ?.let { finalPos = it.positions[entity]!!.position }

            nativeEntity.velocity = Vector(0.0, 0.0, 0.0)
            finalPos?.let { previousLoc ->
                if (shouldSpawn) {
                    this.spawnEntity(entity, previousLoc)
                }
            }
        }
    }

    override fun spawnEntity(
        entity: RecordableEntity,
        position: RecordablePosition,
        velocity: RecordableVector
    ) {

        replayEntities[entity.id]?.takeIf { !it.isRemoved }?.remove()
        val spawnPosition = position.toMinestom()
        val minestomEntity =
            EntityHelper.createEntity(
                Registries.getEntityType(entity.type)!!,
                spawnPosition,
                entity.entityData,
                session.replay.hasChunks
            )
        replayEntities[entity.id] = minestomEntity

        if (minestomEntity.instance != session.world)
            minestomEntity.setInstance(session.world)

        refreshPosition(minestomEntity, spawnPosition)
        minestomEntity.velocity = velocity.toMinestom()

        session.viewers.forEach {
            minestomEntity.addViewer(it)
        }
    }

    override fun refreshPosition(
        minestomEntity: Entity,
        position: Position
    ) {
        minestomEntity.velocity = Vector(0.0, 0.0, 0.0)
        minestomEntity.refreshPosition(position)
        minestomEntity.refreshView(position.yaw, position.pitch)
        minestomEntity.askSynchronization()
    }

    override fun getNativeEntity(entity: RecordableEntity): Entity? {
        return replayEntities[entity.id]
    }

    override fun removeEntity(entity: RecordableEntity) {
        getNativeEntity(entity)?.remove()
        replayEntities.remove(entity.id)
    }

    override fun removeNativeEntity(entity: Entity) {
        entity.remove()
        replayEntities.remove(entity.entityId)
    }

    override fun removeAllEntities() {
        replayEntities.forEach {
            if (it.value !is Player || it.value is ReplayPlayerEntity)
                it.value.remove()
        }
        replayEntities.clear()
    }

    override fun removeEntityViewer(player: Player) {
        replayEntities.forEach {
            it.value.removeViewer(player)
        }
    }
}*/
