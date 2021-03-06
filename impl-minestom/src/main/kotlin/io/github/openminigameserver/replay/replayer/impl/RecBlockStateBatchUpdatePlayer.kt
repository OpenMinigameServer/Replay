package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecBlockStateBatchUpdate
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecBlockStateBatchUpdatePlayer : MinestomActionPlayer<RecBlockStateBatchUpdate> {
    override fun play(
        action: RecBlockStateBatchUpdate,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        val batch = instance.createBlockBatch()
        action.actions.forEach {
            batch.setBlockStateId(it.position.toMinestom().toBlockPosition(), it.newState)
        }
        batch.flush {}
    }
}