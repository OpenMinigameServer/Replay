package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.RecordableAction

enum class SoundCategory {
    MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE
}

class RecSoundEffect(
    var soundId: Int = 0,
    var soundCategory: SoundCategory? = null,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var volume: Float = 0f,
    var pitch: Float = 0f,
) : RecordableAction()