package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntityMetadata(val metadata: ByteArray, entity: RecordableEntity) : EntityRecordableAction(entity)