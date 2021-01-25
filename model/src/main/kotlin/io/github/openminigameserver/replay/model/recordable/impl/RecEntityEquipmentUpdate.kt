package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.entity.data.EquipmentEntityData
import io.github.openminigameserver.replay.model.recordable.reverse.ApplyLastReversible
import io.github.openminigameserver.replay.model.recordable.reverse.DefaultStateReversible


class RecEntityEquipmentUpdate(entity: RecordableEntity, val equipment: Map<EntityEquipmentSlot, RecordableItemStack>) :
    EntityRecordableAction(entity), DefaultStateReversible, ApplyLastReversible {

    override fun provideDefaultState(): RecordableAction {
        return RecEntityEquipmentUpdate(
            entity,
            (entity.entityData as EquipmentEntityData).equipment
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecEntityEquipmentUpdate

        if (entity != other.entity) return false

        return true
    }

    override fun hashCode(): Int {
        return entity.hashCode()
    }


}