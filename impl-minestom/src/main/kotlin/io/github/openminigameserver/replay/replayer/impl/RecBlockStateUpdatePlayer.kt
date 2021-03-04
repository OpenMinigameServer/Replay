package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecBlockStateUpdate
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecBlockStateUpdatePlayer : MinestomActionPlayer<RecBlockStateUpdate> {
    override fun play(action: RecBlockStateUpdate, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        instance.setBlockStateId(action.position.toMinestom().toBlockPosition(), action.newState)
    }
}