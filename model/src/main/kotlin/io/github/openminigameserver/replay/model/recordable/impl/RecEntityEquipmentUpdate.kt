package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity


enum class EntityEquipmentSlot {
    MAIN_HAND,
    OFF_HAND,
    BOOTS,
    LEGGINGS,
    CHESTPLATE,
    HELMET
}

class RecEntityEquipmentUpdate(entity: RecordableEntity, val equipment: Map<EntityEquipmentSlot, RecordableItemStack>) : EntityRecordableAction(entity)