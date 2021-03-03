package io.github.openminigameserver.replay.abstraction

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import java.util.*

/**
 * Represents a world on where a replay will be recorded or replayed
 */
abstract class ReplayWorld {
    abstract val uuid: UUID
    val replaySession: AbstractReplaySession? = null
    val recorder: ReplayRecorder? = null

    abstract val entities: Iterable<ReplayEntity>
    abstract val chunks: Iterable<ReplayChunk>
}