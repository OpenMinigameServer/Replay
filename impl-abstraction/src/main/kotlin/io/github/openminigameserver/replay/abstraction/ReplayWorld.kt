package io.github.openminigameserver.replay.abstraction

import io.github.openminigameserver.replay.recorder.ReplayRecorder
import io.github.openminigameserver.replay.replayer.ReplaySession
import java.util.*

/**
 * Represents a world on where a replay will be recorded or replayed
 */
abstract class ReplayWorld {
    abstract val uuid: UUID
    var replaySession: ReplaySession? = null
    var recorder: ReplayRecorder? = null

    abstract val entities: Iterable<ReplayEntity>
    abstract val chunks: Iterable<ReplayChunk>
}