package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.reverse.Reversible
import kotlin.time.Duration

class RecEntityRemove(
    val position: RecordablePosition,
    entity: RecordableEntity
) : EntityRecordableAction(entity), Reversible {
    override fun provideRevertedActions(start: Duration, end: Duration, session: AbstractReplaySession): List<RecordableAction> = listOf(RecEntitySpawn(position, entity))
}