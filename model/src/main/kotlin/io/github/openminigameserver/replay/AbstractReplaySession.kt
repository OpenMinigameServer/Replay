package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import java.util.*
import kotlin.time.Duration

abstract class AbstractReplaySession {
    abstract val replay: Replay
    var isInitialized = false
    abstract val hasEnded: Boolean
    protected val actions = Stack<RecordableAction>()

    /**
     * Current replay time.
     */
    abstract var time: Duration

    fun findManyActions(
        startDuration: Duration = time,
        targetDuration: Duration = Duration.ZERO,
        condition: (RecordableAction) -> Boolean = { true }
    ): List<RecordableAction> {
        var start = startDuration
        var end = targetDuration
        val isReverse = start > end

        if (isReverse) {
            start = end.also { end = start }
        }

        val actions = replay.actions.filter { it.timestamp in start..end }
        return actions.filter { condition(it) }
            .let { action -> if (isReverse) action.sortedByDescending { it.timestamp } else action.sortedBy { it.timestamp } }
    }


    inline fun <reified T : RecordableAction> findLastAction(
        startDuration: Duration = time,
        targetDuration: Duration = Duration.ZERO,
        condition: (T) -> Boolean = { true }
    ): T? {
        var start = startDuration
        var end = targetDuration
        val isReverse = start > end

        if (isReverse) {
            start = end.also { end = start }
        }

        return replay.actions.filter { it.timestamp in start..end }
            .let { action -> if (isReverse) action.sortedByDescending { it.timestamp } else action.sortedBy { it.timestamp } }
            .lastOrNull { it is T && condition(it) } as? T
    }


    inline fun <reified T : RecordableAction> findManyActionsGeneric(
        startDuration: Duration = time,
        targetDuration: Duration = Duration.ZERO,
        crossinline condition: (T) -> Boolean = { true }
    ): List<T> {
        return findManyActions(startDuration, targetDuration) {
            it is T && condition(it)
        }.map { it as T }
    }

    inline fun <reified T : EntityRecordableAction> findActionsForEntity(
        startDuration: Duration = time,
        entity: RecordableEntity,
        targetDuration: Duration = Duration.ZERO,
        condition: (T) -> Boolean = { true }
    ): T? {
        return findLastAction(startDuration, targetDuration) { it.entity == entity && condition(it) }
    }

    abstract fun init()

    abstract fun unInit()

    abstract fun tick(forceTick: Boolean = false, isTimeStep: Boolean = false)

    abstract fun playAction(action: RecordableAction)
}