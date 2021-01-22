package io.github.openminigameserver.replay.model.recordable

import kotlin.time.Duration

abstract class RecordableAction {
    var timestamp: Duration = Duration.ZERO
}
