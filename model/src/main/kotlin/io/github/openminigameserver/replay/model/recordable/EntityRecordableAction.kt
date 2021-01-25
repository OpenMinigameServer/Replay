package io.github.openminigameserver.replay.model.recordable

import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

abstract class EntityRecordableAction(val entity: RecordableEntity) : RecordableAction() {
}