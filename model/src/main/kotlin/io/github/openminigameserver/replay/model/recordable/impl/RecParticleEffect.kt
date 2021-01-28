package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction

class RecParticleEffect(
    val particleId: Int = 0,
    val longDistance: Boolean = false,
    val x: Double = 0.0,
    var y: kotlin.Double = 0.0,
    var z: kotlin.Double = 0.0,
    val offsetX: Float = 0f,
    var offsetY: kotlin.Float = 0f,
    var offsetZ: kotlin.Float = 0f,
    val particleData: Float = 0f,
    val particleCount: Int = 0,
    val extraData: ByteArray? = null
) : RecordableAction()