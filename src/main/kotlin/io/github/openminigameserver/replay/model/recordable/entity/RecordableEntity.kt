package io.github.openminigameserver.replay.model.recordable.entity

import io.github.openminigameserver.replay.model.recordable.RecordablePosition

data class RecordableEntity(
    val id: Int,
    val type: String,
    val spawnPosition: RecordablePosition?,
    val entityData: Any? = null
) {
    val spawnOnStart: Boolean = spawnPosition != null
}