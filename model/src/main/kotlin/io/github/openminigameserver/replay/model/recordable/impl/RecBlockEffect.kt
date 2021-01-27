package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordablePosition

class RecBlockEffect(
    val effectId: Int,
    val position: RecordablePosition,
    val data: Int,
    val disableRelativeVolume: Boolean
) : RecordableAction()