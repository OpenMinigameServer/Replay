package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.*
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.minestom.MinestomAudiences
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.Component.text
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.PlayerHeadMeta
import java.util.*

internal val audiences = MinestomAudiences.create()

class MinestomReplayUser(val replayPlatform: MinestomReplayPlatform, val player: Player) : ReplayUser(),
    ReplayEntity by MinestomReplayEntity(replayPlatform, player) {
    override var exp: Float
        get() = player.exp
        set(value) {
            player.exp = value
        }
    override val heldSlot: Byte
        get() = player.heldSlot
    override var isFlying: Boolean
        get() = player.isFlying
        set(value) {
            player.isFlying = value
        }
    override var isAllowFlying: Boolean
        get() = player.isFlying
        set(value) {
            player.isFlying = value
        }
    override var gameMode: ReplayGameMode
        get() = ReplayGameMode.valueOf(player.gameMode.name)
        set(value) {
            player.gameMode = GameMode.valueOf(value.name)
        }
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
        player.inventory.setItemStack(slot, itemStack.toMinestom())
    }
}

private fun ReplayActionItemStack.toMinestom(): ItemStack {
    val material = when (action) {
        ControlItemAction.NONE -> Material.AIR

        ControlItemAction.PAUSE -> Material.PINK_DYE
        ControlItemAction.RESUME -> Material.GRAY_DYE
        ControlItemAction.PLAY_AGAIN -> Material.LIME_DYE

        else -> Material.PLAYER_HEAD
    }

    return ItemStack(material, 1).apply {
        val itemSkin = skin
        if (itemSkin != null) {
            itemMeta = PlayerHeadMeta().apply {
                setSkullOwner(UUID.randomUUID())
                setPlayerSkin(PlayerSkin(itemSkin.value, itemSkin.signature))
            }
        }

        displayName = MinestomComponentSerializer.get().serialize(text("§r").append(title))
        controlItemAction = action
    }
}
