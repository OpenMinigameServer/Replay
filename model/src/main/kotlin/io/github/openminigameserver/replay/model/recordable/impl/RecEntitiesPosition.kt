package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.io.entity.RecordableEntityAsKey
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class RecEntitiesPosition(
    @RecordableEntityAsKey
    val positions: MutableMap<RecordableEntity, RecordablePosition>) : RecordableAction()