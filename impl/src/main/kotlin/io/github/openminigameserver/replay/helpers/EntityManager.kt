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
    //Replay entity id
    private val replayEntities = mutableMapOf<Int, Entity>()

    fun resetEntity(entity: RecordableEntity, targetReplayTime: Duration) {
        getNativeEntity(entity)?.let {
            it.remove()
            it.askSynchronization()
            var finalPos = entity.spawnPosition
            session.findPreviousForEntity<RecEntityMove>(entity, targetReplayTime)?.let { finalPos = it.data.position }
            session.findPrevious<RecEntitiesPosition>(targetReplayTime) { it.positions.containsKey(entity) }
                ?.let { finalPos = it.positions[entity]!!.position }
            it.velocity = Vector(0F, 0F, 0F)
            finalPos?.let { previousLoc ->
                this.spawnEntity(entity, previousLoc)
            }
        }
    }

    fun spawnEntity(entity: RecordableEntity, position: RecordablePosition) {

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

    fun removeAllEntities() {
        replayEntities.forEach {
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