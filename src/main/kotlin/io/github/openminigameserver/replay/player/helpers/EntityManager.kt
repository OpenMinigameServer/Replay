package io.github.openminigameserver.replay.player.helpers

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.registry.Registries
import net.minestom.server.utils.Position

class EntityManager(var session: ReplaySession) {
    //Replay entity id
    private val replayEntities = mutableMapOf<Int, Entity>()

    fun resetEntity(entity: RecordableEntity) {
        getNativeEntity(entity)?.let { entity.spawnPosition?.toMinestom()?.let { spawnPos -> refreshPosition(it, spawnPos) } }
    }

    fun spawnEntity(entity: RecordableEntity, position: RecordablePosition) {

        val spawnPosition = position.toMinestom()
        val minestomEntity = EntityHelper.createEntity(Registries.getEntityType(entity.type)!!, spawnPosition, entity.entityData)
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
        spawnPosition: Position
    ) {
        minestomEntity.refreshPosition(spawnPosition)
        minestomEntity.setView(spawnPosition.yaw, spawnPosition.pitch)
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