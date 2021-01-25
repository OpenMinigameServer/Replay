package io.github.openminigameserver.replay.model.recordable.reverse

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import kotlin.time.Duration
import kotlin.time.milliseconds

interface DefaultStateReversible : Reversible {
    @JvmDefault
    override fun provideRevertedActions(
        start: Duration,
        end: Duration,
        session: AbstractReplaySession
    ): List<RecordableAction> {
        val forwardStep = end > start
        return session.findManyActions(
            Duration.ZERO,
            end.let { if (!forwardStep) it - 1.milliseconds else it }
        ) {
            it is DefaultStateReversible && this.isMatch(it) }
            .takeIf { it.isNotEmpty() } ?: listOf(provideDefaultState())

    }

    fun provideDefaultState(): RecordableAction

    fun isMatch(other: RecordableAction): Boolean {
        return this == other
    }

}