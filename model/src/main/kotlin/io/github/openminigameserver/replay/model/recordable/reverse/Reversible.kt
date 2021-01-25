package io.github.openminigameserver.replay.model.recordable.reverse

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import kotlin.time.Duration

interface Reversible {

    fun provideRevertedActions(start: Duration, end: Duration, session: AbstractReplaySession): List<RecordableAction>

}