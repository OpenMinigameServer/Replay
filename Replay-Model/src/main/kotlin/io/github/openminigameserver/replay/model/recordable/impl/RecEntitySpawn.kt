package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntitySpawn constructor(
    val position: RecordablePosition, entity: RecordableEntity
) : EntityRecordableAction(entity)
