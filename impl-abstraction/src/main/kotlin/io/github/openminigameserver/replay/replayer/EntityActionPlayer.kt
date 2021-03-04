package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

abstract class EntityActionPlayer<T : EntityRecordableAction, W : ReplayWorld, P : ReplayUser, E : ReplayEntity> :
    ActionPlayer<T, W, P> {
    @Suppress("UNCHECKED_CAST")
    override fun play(action: T, session: ReplaySession, instance: W, viewers: List<P>) {
        val entity = session.entityManager.getNativeEntity(action.entity) as? E
        if (entity != null) {
            play(action, action.entity, entity, session, instance, viewers)
        }
    }

    abstract fun play(
        action: T,
        replayEntity: RecordableEntity,
        nativeEntity: E,
        session: ReplaySession,
        instance: W,
        viewers: List<P>
    )
}