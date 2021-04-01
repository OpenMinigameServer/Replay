package io.github.openminigameserver.replay.model.recordable.entity.data

import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot

data class PlayerEntityData(
    var userName: String, var skin: PlayerSkinData?, val metadata: ByteArray,
    override val equipment: Map<EntityEquipmentSlot, RecordableItemStack>
) :
    BaseEntityData(), EquipmentEntityData {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerEntityData

        if (userName != other.userName) return false
        if (skin != other.skin) return false
        if (!metadata.contentEquals(other.metadata)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + (skin?.hashCode() ?: 0)
        result = 31 * result + metadata.contentHashCode()
        return result
    }
}
