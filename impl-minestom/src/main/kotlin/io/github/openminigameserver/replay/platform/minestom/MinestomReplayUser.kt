package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.minestom.MinestomAudiences
import net.minestom.server.entity.Player
import java.util.*

internal val audiences = MinestomAudiences.create()

class MinestomReplayUser(val player: Player) : ReplayUser() {
    override val audience: Audience = audiences.player(player)

    override val name: String
        get() = player.username

    override val id: Int
        get() = player.entityId

    override val uuid: UUID
        get() = player.uuid

    override val position: RecordablePosition
        get() = player.position.toReplay()

    override val velocity: RecordableVector
        get() = player.velocity.toReplay()

    override val world: ReplayWorld
        get() = MinestomReplayWorld(player.instance!!)

    override fun getEquipment(): Map<EntityEquipmentSlot, RecordableItemStack> {
        TODO("Not yet implemented")
    }
}