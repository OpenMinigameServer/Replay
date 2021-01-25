package io.github.openminigameserver.replay.model.recordable

data class RecordableItemStack(val nbtValue: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordableItemStack

        if (!nbtValue.contentEquals(other.nbtValue)) return false

        return true
    }

    override fun hashCode(): Int {
        return nbtValue.contentHashCode()
    }
}