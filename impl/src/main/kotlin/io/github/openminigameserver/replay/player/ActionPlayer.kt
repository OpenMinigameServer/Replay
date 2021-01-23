package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

interface ActionPlayer<T : RecordableAction> {
    fun play(action: T, session: ReplaySession, instance: Instance, viewers: List<Player>)
}

