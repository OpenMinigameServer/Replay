package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import kotlin.time.Duration

interface IEntityManager<P : ReplayUser, E : ReplayEntity> {
    var session: ReplaySession
    val entities: Collection<RecordableEntity>

    //Replay entity id
    val replayEntities: MutableMap<Int, E>

    fun resetEntity(entity: RecordableEntity, startTime: Duration, targetReplayTime: Duration) {
        getNativeEntity(entity)?.let { e ->
            removeEntity(entity, false)

            //Check if Entity (has been spawned at start) or (has been spawned somewhere before and has not been removed before)
            val shouldSpawn = true

            //Find actual position
            var finalPos = entity.spawnPosition?.position
            session.findActionsForEntity<RecEntityMove>(startTime, entity, targetReplayTime)
                ?.let { finalPos = it.data.position }
            session.findLastAction<RecEntitiesPosition>(
                startTime,
                targetReplayTime
            ) { it.positions.containsKey(entity) }
                ?.let { finalPos = it.positions[entity]!!.position }

            e.velocity = RecordableVector(0.0, 0.0, 0.0)
            finalPos?.let { previousLoc ->
                if (shouldSpawn) {
                    this.spawnEntity(entity, previousLoc)
                }
            }
        }
    }

    fun spawnEntity(
        entity: RecordableEntity,
        position: RecordablePosition,
        velocity: RecordableVector = RecordableVector(0.0, 0.0, 0.0)
    )

    fun refreshPosition(
        entity: E,
        position: RecordablePosition
    )

    fun getNativeEntity(entity: RecordableEntity): E?
    fun removeEntity(entity: RecordableEntity, destroy: Boolean = true)
    fun removeNativeEntity(entity: E, destroy: Boolean)
    fun removeAllEntities()
    fun removeEntityViewer(player: P)
}