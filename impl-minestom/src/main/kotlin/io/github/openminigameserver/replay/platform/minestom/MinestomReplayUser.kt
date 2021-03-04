package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.*
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.minestom.MinestomAudiences
import net.minestom.server.entity.Player

internal val audiences = MinestomAudiences.create()

class MinestomReplayUser(val replayPlatform: MinestomReplayPlatform, val player: Player) : ReplayUser(),
    ReplayEntity by MinestomReplayEntity(replayPlatform, player) {
    override var exp: Float
        get() = TODO("Not yet implemented")
        set(value) {}
    override val heldSlot: Byte
        get() = TODO("Not yet implemented")
    override var isFlying: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isAllowFlying: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gameMode: ReplayGameMode
        get() = TODO("Not yet implemented")
        set(value) {}
    override val audience: Audience = audiences.player(player)
    override val name: String
        get() = player.username

    override fun setWorld(instance: ReplayWorld) {
        (instance as? MinestomReplayWorld)?.instance?.let { player.setInstance(it) }
    }

    override fun clearInventory() {
        player.inventory.clear()
    }

    override fun setHeldItemSlot(slot: Byte) {
        player.setHeldItemSlot(slot)
    }

    override fun setItemStack(slot: Int, itemStack: ReplayActionItemStack) {
        TODO("Not yet implemented")
    }
}