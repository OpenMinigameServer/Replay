package io.github.openminigameserver.replay.model.recordable

data class RecordedChunk(val chunkX: Int, val chunkZ: Int, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordedChunk

        if (chunkX != other.chunkX) return false
        if (chunkZ != other.chunkZ) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunkX
        result = 31 * result + chunkZ
        result = 31 * result + data.contentHashCode()
        return result
    }
}
