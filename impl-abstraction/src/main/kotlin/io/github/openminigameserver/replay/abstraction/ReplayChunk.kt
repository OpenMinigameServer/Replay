package io.github.openminigameserver.replay.abstraction

interface ReplayChunk {
    val x: Int
    val z: Int
    val serializedData: ByteArray?
}