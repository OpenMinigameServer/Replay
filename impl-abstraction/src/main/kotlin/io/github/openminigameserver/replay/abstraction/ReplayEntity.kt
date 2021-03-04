package io.github.openminigameserver.replay.abstraction

import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.platform.ReplayPlatform
import java.util.*

interface ReplayEntity {
    val id: Int
    val uuid: UUID
    val position: RecordablePosition
    val velocity: RecordableVector
    val world: ReplayWorld
    val instance: ReplayWorld get() = world

    fun toReplay(replayPlatform: ReplayPlatform<ReplayWorld, ReplayUser, ReplayEntity>): RecordableEntity {
        return RecordableEntity(
            id,
            replayPlatform.getEntityType(this),
            RecordablePositionAndVector(position, velocity),
            replayPlatform.getEntityData(this)
        )
    }

    fun getEquipment(): Map<EntityEquipmentSlot, RecordableItemStack>
    fun teleport(position: RecordablePosition)
}