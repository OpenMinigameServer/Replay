package io.github.openminigameserver.replay.platform.minestom.replayer

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayUser
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayWorld
import io.github.openminigameserver.replay.replayer.ActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

interface MinestomActionPlayer<T : RecordableAction> : ActionPlayer<T, MinestomReplayWorld, MinestomReplayUser> {
    fun play(action: T, session: ReplaySession, instance: Instance, viewers: List<Player>)

    override fun play(
        action: T,
        session: ReplaySession,
        instance: MinestomReplayWorld,
        viewers: List<MinestomReplayUser>
    ) {
        play(action, session, instance.instance, viewers.map { it.player })
    }
}