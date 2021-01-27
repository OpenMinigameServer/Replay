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


        val reversibleActions: List<RecordableAction> =
            session.findManyActions(start, end) { it is Reversible }.groupBy { it.javaClass }
                .flatMap { it.value }.map { it as Reversible }
                .flatMap { entry ->
                    if (!isForwardStep) entry.provideRevertedActions(start, end, session) else listOf(
                        entry as RecordableAction
                    )
                }

        val actionsToPlay = mutableListOf<RecordableAction>()
        actionsToPlay.addAll(reversibleActions.toMutableList())

        if (isForwardStep) actionsToPlay.sortBy { it.timestamp } else actionsToPlay.sortByDescending { it.timestamp }

        actionsToPlay.filter { it is Reversible && it.isAppliedInBatch }.groupBy { it.javaClass }
            .forEach { groupedEntry ->
                val first = groupedEntry.value.firstOrNull() as? Reversible ?: return@forEach
                actionsToPlay.removeAll(groupedEntry.value)
                first.batchActions(groupedEntry.value.let { if (isForwardStep) it.sortedBy { it.timestamp } else it.sortedByDescending { it.timestamp } })
                    ?.let { it1 -> actionsToPlay.add(it1) }
            }

        //Reset entities first, then play actions
        entityManager.entities.forEach {
            entityManager.resetEntity(it, start, end)
        }

        actionsToPlay.forEach {
            session.playAction(it)
        }

    }
}