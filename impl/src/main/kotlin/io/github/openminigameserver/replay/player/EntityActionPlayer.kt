package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

abstract class EntityActionPlayer<T : EntityRecordableAction> : ActionPlayer<T> {
    override fun play(action: T, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        val entity = session.entityManager.getNativeEntity(action.entity)
        if (entity != null) {
            play(action, entity, session, instance, viewers)
        }
    }

    abstract fun play(action: T, entity: Entity, session: ReplaySession, instance: Instance, viewers: List<Player>)
}