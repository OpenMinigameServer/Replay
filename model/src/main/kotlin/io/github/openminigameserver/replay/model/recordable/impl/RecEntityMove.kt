package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntityMove(val data: RecordablePositionAndVector, entity: RecordableEntity) : EntityRecordableAction(entity)

