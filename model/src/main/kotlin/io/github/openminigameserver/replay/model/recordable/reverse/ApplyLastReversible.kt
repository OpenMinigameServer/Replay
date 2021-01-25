package io.github.openminigameserver.replay.model.recordable.reverse

import io.github.openminigameserver.replay.model.recordable.RecordableAction


/**
 * Helper class implementing a batch that will only return the last value of this group
 */
interface ApplyLastReversible : Reversible {

    override val isAppliedInBatch: Boolean
        get() = true

    override fun batchActions(value: List<RecordableAction>): RecordableAction? {
        return value.last()
    }
}