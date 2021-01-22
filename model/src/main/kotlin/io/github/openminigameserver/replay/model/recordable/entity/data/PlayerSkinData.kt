package io.github.openminigameserver.replay.model.recordable.entity.data

data class PlayerSkinData(val textures: ByteArray, val signature: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerSkinData

        if (!textures.contentEquals(other.textures)) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textures.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}