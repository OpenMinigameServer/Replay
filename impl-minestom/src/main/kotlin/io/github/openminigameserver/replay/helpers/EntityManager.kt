package io.github.openminigameserver.replay.helpers

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayEntity
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayPlatform
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayUser
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayWorld
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.registry.Registries
import net.minestom.server.utils.Vector
import kotlin.time.Duration

class EntityManager(val platform: MinestomReplayPlatform, override var session: ReplaySession) :
    IEntityManager<MinestomReplayUser, MinestomReplayEntity> {
    override val entities: Collection<RecordableEntity>
        get() = replayEntities.keys.mapNotNull { session.replay.getEntityById(it) }

    //Replay entity id
    override val replayEntities = mutableMapOf<Int, MinestomReplayEntity>()

    override fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration) {
        getNativeEntity(entity)?.let { e ->
            val nativeEntity = e.entity
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
        replayEntities[entity.id]?.takeIf { !it.entity.isRemoved }?.entity?.remove()
        val spawnPosition = position.toMinestom()
        val minestomEntity =
            MinestomReplayEntity(
                platform,
                EntityHelper.createEntity(
                    Registries.getEntityType(entity.type)!!,
                    spawnPosition,
                    entity.entityData,
                    session.replay.hasChunks
                )
            )
        replayEntities[entity.id] = minestomEntity

        if (minestomEntity.instance != session.world)
            minestomEntity.entity.setInstance((session.world as MinestomReplayWorld).instance)

        refreshPosition(minestomEntity, spawnPosition.toReplay())
        minestomEntity.velocity = velocity

        session.viewers.forEach {
            minestomEntity.entity.addViewer((it as MinestomReplayUser).player)
        }
    }

    override fun refreshPosition(entity: MinestomReplayEntity, position: RecordablePosition) {
        val minestomEntity = entity.entity
        minestomEntity.velocity = Vector(0.0, 0.0, 0.0)
        minestomEntity.refreshPosition(position.toMinestom())
        minestomEntity.refreshView(position.yaw, position.pitch)
        minestomEntity.askSynchronization()
    }

    override fun getNativeEntity(entity: RecordableEntity): MinestomReplayEntity? {
        return replayEntities[entity.id]
    }

    override fun removeEntity(entity: RecordableEntity) {
        getNativeEntity(entity)?.entity?.remove()
        replayEntities.remove(entity.id)
    }

    override fun removeNativeEntity(entity: MinestomReplayEntity) {
        entity.entity.remove()
        replayEntities.remove(entity.id)
    }

    override fun removeAllEntities() {
        replayEntities.forEach {
            val entity = it.value.entity
            if (entity !is Player || entity is ReplayPlayerEntity)
                entity.remove()
        }
        replayEntities.clear()
    }

    override fun removeEntityViewer(player: MinestomReplayUser) {
        replayEntities.forEach {
            it.value.entity.removeViewer(player.player)
        }
    }
}
