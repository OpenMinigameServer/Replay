package io.github.openminigameserver.replay

import java.util.*

/**
 * Represents a world on where a replay will be recorded or replayed
 */
abstract class ReplayWorld {
    abstract val uuid: UUID
    val replaySession: AbstractReplaySession? = null

}