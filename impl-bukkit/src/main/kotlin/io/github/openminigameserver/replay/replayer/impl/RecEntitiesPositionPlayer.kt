package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayEntity
import io.github.openminigameserver.replay.replayer.BukkitActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import io.github.openminigameserver.replay.runSync
import io.github.openminigameserver.replay.toBukkit
import org.bukkit.World
import org.bukkit.entity.Player

object RecEntitiesPositionPlayer : BukkitActionPlayer<RecEntitiesPosition> {
    override fun play(action: RecEntitiesPosition, session: ReplaySession, instance: World, viewers: List<Player>) {
        runSync {
            action.positions.forEach {
                val replayEntity = session.entityManager.getNativeEntity(it.key) as? BukkitReplayEntity
                val entity =
                    replayEntity?.entity ?: return@forEach

                val data = it.value
                val velocity = data.velocity.toBukkit()
                session.entityManager.refreshPosition(replayEntity, data.position)
                entity.velocity = velocity
            }
        }
    }

}