package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomEntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecEntityMovePlayer : MinestomEntityActionPlayer<RecEntityMove>() {
    override fun play(
        action: RecEntityMove,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        val data = action.data
        val position = data.position.toMinestom()
        val velocity = data.velocity.toMinestom()
        nativeEntity.refreshPosition(position)
        nativeEntity.refreshView(position.yaw, position.pitch)
        nativeEntity.askSynchronization()
        nativeEntity.velocity = velocity
    }
}