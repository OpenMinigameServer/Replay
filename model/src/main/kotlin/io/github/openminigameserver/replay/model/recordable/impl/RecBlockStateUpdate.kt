package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition

class RecBlockStateUpdate(val position: RecordablePosition, val oldState: Short, val newState: Short) :
    RecordableAction() {
    override val isReversible: Boolean
        get() = true

    override val reversedAction: List<RecordableAction>
        get() = listOf(RecBlockStateUpdate(position, newState, oldState))
}