package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntityMove(val position: RecordablePosition, entity: RecordableEntity) : EntityRecordableAction(entity)

