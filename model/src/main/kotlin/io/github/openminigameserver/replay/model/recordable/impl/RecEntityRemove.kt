package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntityRemove(
    val position: RecordablePosition,
    entity: RecordableEntity
) : EntityRecordableAction(entity) {
    override val isReversible: Boolean
        get() = true

    override val reversedAction: List<RecordableAction>
       get() = listOf(RecEntitySpawn(position, entity))
}