package io.github.openminigameserver.replay.model.recordable.reverse

import io.github.openminigameserver.replay.model.recordable.RecordableAction

interface ApplyLastReversible : Reversible {
    override val isAppliedInBatch: Boolean
        get() = true

    override fun batchActions(value: List<RecordableAction>): RecordableAction? {
        return value.last()
    }
}