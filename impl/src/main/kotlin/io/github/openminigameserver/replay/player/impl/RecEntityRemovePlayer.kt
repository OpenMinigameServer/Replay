package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecEntityRemove
import io.github.openminigameserver.replay.player.EntityActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecEntityRemovePlayer : EntityActionPlayer<RecEntityRemove>() {
    override fun play(
        action: RecEntityRemove,
        entity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        entity.remove()
    }
}