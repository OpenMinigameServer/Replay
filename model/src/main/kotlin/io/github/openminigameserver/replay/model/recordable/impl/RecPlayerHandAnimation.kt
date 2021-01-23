package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

enum class Hand {
    MAIN, OFF
}

class RecPlayerHandAnimation(val hand: Hand, entity: RecordableEntity) : EntityRecordableAction(entity)