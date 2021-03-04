package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityRemove
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomEntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecEntityRemovePlayer : MinestomEntityActionPlayer<RecEntityRemove>() {
    override fun play(
        action: RecEntityRemove,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        session.entityManager.removeEntity(replayEntity)
    }
}