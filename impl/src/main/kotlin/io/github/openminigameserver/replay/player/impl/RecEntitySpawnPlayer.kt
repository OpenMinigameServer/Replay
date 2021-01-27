package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecEntitySpawn
import io.github.openminigameserver.replay.player.ActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecEntitySpawnPlayer : ActionPlayer<RecEntitySpawn> {
    override fun play(action: RecEntitySpawn, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        session.entityManager.spawnEntity(action.entity, action.positionAndVelocity.position, action.positionAndVelocity.velocity)
    }
}