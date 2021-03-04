package io.github.openminigameserver.replay.platform.minestom.replayer

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayEntity
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayUser
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayWorld
import io.github.openminigameserver.replay.replayer.EntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

abstract class MinestomEntityActionPlayer<T : EntityRecordableAction> :
    EntityActionPlayer<T, MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity>() {
    override fun play(
        action: T,
        replayEntity: RecordableEntity,
        nativeEntity: MinestomReplayEntity,
        session: ReplaySession,
        instance: MinestomReplayWorld,
        viewers: List<MinestomReplayUser>
    ) {
        play(action, replayEntity, nativeEntity.entity, session, instance.instance, viewers.map { it.player })
    }

    abstract fun play(
        action: T,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    )
}