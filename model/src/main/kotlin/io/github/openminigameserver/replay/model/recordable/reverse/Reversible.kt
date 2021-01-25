package io.github.openminigameserver.replay.model.recordable.reverse

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import kotlin.time.Duration

interface Reversible {

    val isAppliedInBatch: Boolean
        get() = false

    fun batchActions(value: List<RecordableAction>): RecordableAction? = null

    fun provideRevertedActions(start: Duration, end: Duration, session: AbstractReplaySession): List<RecordableAction>

}