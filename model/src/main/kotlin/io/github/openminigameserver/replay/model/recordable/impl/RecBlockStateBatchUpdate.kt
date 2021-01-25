package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction


data class RecBlockStateBatchUpdate(val actions: List<RecBlockStateUpdate>) : RecordableAction()