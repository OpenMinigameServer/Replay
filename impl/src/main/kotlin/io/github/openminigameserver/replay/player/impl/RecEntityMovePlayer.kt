package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.player.EntityActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Vector

object RecEntityMovePlayer : EntityActionPlayer<RecEntityMove>() {
    private val emptyVector = Vector()

    override fun play(
        action: RecEntityMove,
        entity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        val data = action.data
        val position = data.position.toMinestom()
        val velocity = data.velocity.toMinestom()
        entity.refreshPosition(position)
        entity.setView(position.yaw, position.pitch)
        entity.velocity = velocity
    }
}