package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.ReplayChunk
import net.minestom.server.instance.Chunk

class MinestomReplayChunk(private val chunk: Chunk) : ReplayChunk {
    override val x: Int
        get() = chunk.chunkX
    override val z: Int
        get() = chunk.chunkZ
    override val serializedData: ByteArray?
        get() = chunk.serializedData
}