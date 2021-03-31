package io.github.openminigameserver.replay.platform.bukkit

import io.github.openminigameserver.replay.abstraction.*
import io.github.openminigameserver.replay.runSync
import io.github.openminigameserver.replay.toBukkit
import net.kyori.adventure.audience.Audience
import org.bukkit.GameMode
import org.bukkit.entity.Player

class BukkitReplayUser(val player: Player) : ReplayUser(), ReplayEntity by BukkitReplayEntity(player) {
    override var exp: Float
        get() = player.exp
        set(value) {
            runSync { player.exp = value }
        }
    override val heldSlot: Byte
        get() = player.inventory.heldItemSlot.toByte()
    override var isFlying: Boolean
        get() = player.isFlying
        set(value) {
            runSync { player.isFlying = value }
        }
    override var isAllowFlying: Boolean
        get() = player.allowFlight
        set(value) {
            runSync { player.allowFlight = value }
        }
    override var gameMode: ReplayGameMode
        get() = ReplayGameMode.valueOf(player.gameMode.name)
        set(value) {
            runSync { player.gameMode = GameMode.valueOf(value.name) }
        }
    override val audience: Audience
        get() = player
    override val name: String
        get() = player.name

    override fun setWorld(instance: ReplayWorld) {
        player.teleportAsync((instance as BukkitReplayWorld).world.spawnLocation)
    }

    override fun clearInventory() {
        player.inventory.clear()
    }

    override fun setHeldItemSlot(count: Byte) {
        player.inventory.heldItemSlot = count.toInt()
    }

    override fun setItemStack(slot: Int, itemStack: ReplayActionItemStack) {
        player.inventory.setItem(slot, itemStack.toBukkit())
    }
}