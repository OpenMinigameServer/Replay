package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecEntitySpawn
import io.github.openminigameserver.replay.replayer.BukkitActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import org.bukkit.World
import org.bukkit.entity.Player

object RecEntitySpawnPlayer : BukkitActionPlayer<RecEntitySpawn> {
    override fun play(action: RecEntitySpawn, session: ReplaySession, instance: World, viewers: List<Player>) {
        session.entityManager.spawnEntity(
            action.entity,
            action.positionAndVelocity.position,
            action.positionAndVelocity.velocity
        )
    }
}