package io.github.openminigameserver.replay.helpers

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.registry.Registries
import net.minestom.server.utils.Position
import net.minestom.server.utils.Vector
import kotlin.time.Duration

class EntityManager(var session: ReplaySession) {
    val entities: Collection<RecordableEntity>
        get() = replayEntities.keys.mapNotNull { session.replay.getEntityById(it) }

    //Replay entity id
    private val replayEntities = mutableMapOf<Int, Entity>()

    fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration) {
        getNativeEntity(entity)?.let {
            it.remove()
            it.askSynchronization()

          /*  val spawnAction = session.findActionsForEntity<RecEntitySpawn>(
                entity = entity,
                startDuration = startTime,
                targetDuration = targetReplayTime
            )
            val removeAction = session.findActionsForEntity<RecEntityRemove>(
                entity = entity,
                startDuration = startTime,
                targetDuration = targetReplayTime
            )*/
            //Check if Entity (has been spawned at start) or (has been spawned somewhere before and has not been removed before)
            val shouldSpawn = true

            //Find actual position
            var finalPos = entity.spawnPosition
            session.findActionsForEntity<RecEntityMove>(startTime, entity, targetReplayTime)
                ?.let { finalPos = it.data.position }
            session.findActions<RecEntitiesPosition>(startTime, targetReplayTime) { it.positions.containsKey(entity) }
                ?.let { finalPos = it.positions[entity]!!.position }

//            println("${entity.type} ${entity.id} $shouldSpawn")
            it.velocity = Vector(0F, 0F, 0F)
            finalPos?.let { previousLoc ->
                if (shouldSpawn) {
                    this.spawnEntity(entity, previousLoc)
                }
            }
        }
    }

    fun spawnEntity(entity: RecordableEntity, position: RecordablePosition) {

        replayEntities[entity.id]?.takeIf { !it.isRemoved }?.remove()
        val spawnPosition = position.toMinestom()
        val minestomEntity =
            EntityHelper.createEntity(Registries.getEntityType(entity.type)!!, spawnPosition, entity.entityData)
        replayEntities[entity.id] = minestomEntity

        if (minestomEntity.instance != session.instance)
            minestomEntity.setInstance(session.instance)

        refreshPosition(minestomEntity, spawnPosition)

        session.viewers.forEach {
            minestomEntity.addViewer(it)
        }
    }

    private fun refreshPosition(
        minestomEntity: Entity,
        position: Position
    ) {
        minestomEntity.velocity = Vector(0F, 0F, 0F)
        minestomEntity.refreshPosition(position)
        minestomEntity.refreshView(position.yaw, position.pitch)
        minestomEntity.askSynchronization()
    }

    fun getNativeEntity(entity: RecordableEntity): Entity? {
        return replayEntities[entity.id]
    }

    fun removeEntity(entity: RecordableEntity) {
        getNativeEntity(entity)?.remove()
        replayEntities.remove(entity.id)
    }
    fun removeNativeEntity(entity: Entity) {
        entity.remove()
        replayEntities.remove(entity.entityId)
    }

    fun removeAllEntities() {
        replayEntities.forEach {
            if (it.value !is Player || it.value is ReplayPlayerEntity)
                it.value.remove()
        }
        replayEntities.clear()
    }

    fun removeEntityViewer(player: Player) {
        replayEntities.forEach {
            it.value.removeViewer(player)
        }
    }
}