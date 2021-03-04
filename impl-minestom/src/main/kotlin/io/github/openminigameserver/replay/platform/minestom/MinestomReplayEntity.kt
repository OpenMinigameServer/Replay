package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import net.minestom.server.entity.Entity
import java.util.*

class MinestomReplayEntity(private val replayPlatform: MinestomReplayPlatform, val entity: Entity) : ReplayEntity {
    override val id: Int
        get() = entity.entityId
    override val uuid: UUID
        get() = entity.uuid
    override val position: RecordablePosition
        get() = entity.position.toReplay()
    override val velocity: RecordableVector
        get() = entity.velocity.toReplay()
    override val world: ReplayWorld
        get() = replayPlatform.getWorldById(entity.instance!!.uniqueId)

    override fun getEquipment(): Map<EntityEquipmentSlot, RecordableItemStack> {
        TODO("Not yet implemented")
    }

    override fun teleport(position: RecordablePosition) {
        entity.teleport(position.toMinestom())
    }
}