package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import kotlin.time.Duration

interface IEntityManager<P : ReplayUser, E : ReplayEntity> {
    var session: ReplaySession
    val entities: Collection<RecordableEntity>

    //Replay entity id
    val replayEntities: MutableMap<Int, E>
    fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration)
    fun spawnEntity(
        entity: RecordableEntity,
        position: RecordablePosition,
        velocity: RecordableVector = RecordableVector(0.0, 0.0, 0.0)
    )

    fun refreshPosition(
        minestomEntity: E,
        position: RecordablePosition
    )

    fun getNativeEntity(entity: RecordableEntity): E?
    fun removeEntity(entity: RecordableEntity)
    fun removeNativeEntity(entity: E)
    fun removeAllEntities()
    fun removeEntityViewer(player: P)
}