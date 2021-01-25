package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import kotlin.time.Duration

class ReplaySessionTimeStepHelper(private val session: ReplaySession) {
    private val replay get() = session.replay
    private val entityManager get() = session.entityManager

    fun performTimeStep(currentTime: Duration, targetReplayTime: Duration) {
        val isForwardStep = targetReplayTime > currentTime

        val (start, end) = currentTime to targetReplayTime

        val reversibleActions =
            session.findManyActions(start, end) { it.isReversible }.groupBy { it.javaClass }
                .flatMap { it.value }
                .flatMap { entry -> if (!isForwardStep) entry.reversedAction else listOf(entry) }
        val actionsToPlay = mutableListOf<RecordableAction>()

        actionsToPlay.addAll(reversibleActions.toMutableList())

        actionsToPlay.forEach {
            println("Playing ${it.javaClass.simpleName}")
            session.playAction(it)
        }

        entityManager.entities.forEach {
            entityManager.resetEntity(it, start, end)
        }

    }
}