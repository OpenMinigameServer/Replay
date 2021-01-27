package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.reverse.Reversible
import kotlin.time.Duration

class RecEntitySpawn constructor(
    val positionAndVelocity: RecordablePositionAndVector, entity: RecordableEntity
) : EntityRecordableAction(entity), Reversible {

    override fun provideRevertedActions(start: Duration, end: Duration, session: AbstractReplaySession): List<RecordableAction> = listOf(RecEntityRemove(positionAndVelocity.position, entity))
}

