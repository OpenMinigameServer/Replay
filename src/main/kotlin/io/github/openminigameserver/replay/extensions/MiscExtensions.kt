package io.github.openminigameserver.replay.extensions

import io.github.openminigameserver.replay.model.ReplayFile
import kotlinx.datetime.Clock
import kotlin.time.Duration

internal val ReplayFile.currentDuration: Duration
    get() {
        return Clock.System.now().minus(recordStartTime)
    }