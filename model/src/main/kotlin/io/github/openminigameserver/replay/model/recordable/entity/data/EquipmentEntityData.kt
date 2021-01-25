package io.github.openminigameserver.replay.model.recordable.entity.data

import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot

interface EquipmentEntityData {
    val equipment: Map<EntityEquipmentSlot, RecordableItemStack>
}