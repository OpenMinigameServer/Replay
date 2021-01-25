package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.reverse.Reversible
import kotlin.time.Duration

class ReplaySessionTimeStepHelper(private val session: ReplaySession) {
    private val replay get() = session.replay
    private val entityManager get() = session.entityManager

    fun performTimeStep(currentTime: Duration, targetReplayTime: Duration) {
        val isForwardStep = targetReplayTime > currentTime
        val (start, end) = currentTime to targetReplayTime


        val reversibleActions =
            session.findManyActions(start, end) { it is Reversible }.groupBy { it.javaClass }
                .flatMap { it.value }.map { it as Reversible }
                .flatMap { entry -> if (!isForwardStep) entry.provideRevertedActions(start, end, session) else listOf(entry as RecordableAction) }

        val actionsToPlay = mutableListOf<RecordableAction>()
        actionsToPlay.addAll(reversibleActions.toMutableList())

        if (isForwardStep) actionsToPlay.sortBy { it.timestamp } else actionsToPlay.sortByDescending { it.timestamp }
        actionsToPlay.forEach {
//            println("Playing ${it.javaClass.simpleName}")
            session.playAction(it)
        }

        entityManager.entities.forEach {
            entityManager.resetEntity(it, start, end)
        }

    }
}