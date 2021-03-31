package io.github.openminigameserver.replay.platform.bukkit

import io.github.openminigameserver.replay.ReplayPlugin
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import io.github.openminigameserver.replay.runSync
import io.github.openminigameserver.replay.toBukkit
import io.github.openminigameserver.replay.toReplay
import org.bukkit.entity.Entity
import java.util.*

class BukkitReplayEntity(val entity: Entity) : ReplayEntity {
    override val id: Int
        get() = entity.entityId
    override val uuid: UUID
        get() = entity.uniqueId
    override val position: RecordablePosition
        get() = entity.location.toReplay()
    override var velocity: RecordableVector
        get() = entity.velocity.toReplay()
        set(value) {
            runSync { entity.velocity = value.toBukkit() }
        }
    override val world: ReplayWorld
        get() = ReplayPlugin.platform.getWorldById(entity.world.uid)

    override fun getEquipment(): Map<EntityEquipmentSlot, RecordableItemStack> {
        TODO("Not yet implemented")
    }

    override fun teleport(position: RecordablePosition) {
        entity.teleportAsync(position.toBukkit(entity.world))
    }
}