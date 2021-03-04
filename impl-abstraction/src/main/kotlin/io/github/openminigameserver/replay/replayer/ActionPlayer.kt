package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.recordable.RecordableAction

interface ActionPlayer<T : RecordableAction, W : ReplayWorld, P : ReplayUser> {
    fun play(action: T, session: ReplaySession, instance: W, viewers: List<P>)
}

