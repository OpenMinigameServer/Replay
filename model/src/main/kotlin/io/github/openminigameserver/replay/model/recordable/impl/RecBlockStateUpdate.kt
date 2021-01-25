package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.reverse.DefaultStateReversible

data class RecBlockStateUpdate(val position: RecordablePosition, val newState: Short) :
    RecordableAction(), DefaultStateReversible {
    override val isAppliedInBatch: Boolean
        get() = true

    override fun batchActions(value: List<RecordableAction>): RecordableAction {
        return RecBlockStateBatchUpdate(value.mapNotNull { it as? RecBlockStateUpdate })
    }

    override fun provideDefaultState(): RecordableAction {
        return RecBlockStateUpdate(position, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecBlockStateUpdate

        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}