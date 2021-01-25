package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecBlockStateUpdate
import io.github.openminigameserver.replay.player.ActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecBlockStateUpdatePlayer : ActionPlayer<RecBlockStateUpdate> {
    override fun play(action: RecBlockStateUpdate, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        instance.setBlockStateId(action.position.toMinestom().toBlockPosition(), action.newState)
    }
}