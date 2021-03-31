package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayUser
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayWorld
import org.bukkit.World
import org.bukkit.entity.Player

interface BukkitActionPlayer<T : RecordableAction> : ActionPlayer<T, BukkitReplayWorld, BukkitReplayUser> {
    fun play(action: T, session: ReplaySession, instance: World, viewers: List<Player>)

    override fun play(
        action: T,
        session: ReplaySession,
        instance: BukkitReplayWorld,
        viewers: List<BukkitReplayUser>
    ) {
        play(action, session, instance.world, viewers.map { it.player })
    }
}