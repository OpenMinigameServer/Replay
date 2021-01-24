package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntityRemove constructor(
    entity: RecordableEntity
) : EntityRecordableAction(entity)