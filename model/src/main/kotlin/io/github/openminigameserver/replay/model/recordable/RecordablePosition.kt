package io.github.openminigameserver.replay.model.recordable

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class RecordablePosition(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val yaw: Float = 0f,
    val pitch: Float = 0f
)
