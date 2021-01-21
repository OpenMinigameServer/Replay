package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.player.ActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecEntitiesPositionPlayer : ActionPlayer<RecEntitiesPosition> {
    override fun play(action: RecEntitiesPosition, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        action.positions.forEach {
            val entity = session.entityManager.getNativeEntity(it.key) ?: return@forEach

            val position = it.value.toMinestom()
            entity.refreshPosition(position)
            entity.setView(position.yaw, position.pitch)
        }
    }
}