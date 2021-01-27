package io.github.openminigameserver.replay.model.recordable.reverse

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import kotlin.time.Duration

/**
 * Represents an action that is reversible
 */
interface Reversible {

    /**
     * States if actions like this are applied in batch
     */
    @get:JsonIgnore
    val isAppliedInBatch: Boolean
        get() = false

    /**
     * Batch multiple actions of this type into a single action
     */
    fun batchActions(value: List<RecordableAction>): RecordableAction? = null

    /**
     * Provide a list of actions that, when run, revert this action
     */
    fun provideRevertedActions(start: Duration, end: Duration, session: AbstractReplaySession): List<RecordableAction>

}