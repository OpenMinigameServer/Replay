package io.github.openminigameserver.replay.replayer.statehelper.utils

import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList

class ReplayStatePlayerData(
    private val isAllowFlying: Boolean,
    private val isFlying: Boolean,
    private val gameMode: GameMode,
    private val heldSlot: Byte,
    private val exp: Float,
    private val inventory: NBTList<NBTCompound>
) {

    constructor(player: Player) : this(
        player.isAllowFlying,
        player.isFlying,
        player.gameMode,
        player.heldSlot,
        player.exp,
        getPlayerInventoryCopy(player)
    )

    fun apply(player: Player) {
        player.isAllowFlying = isAllowFlying
        player.isFlying = isFlying
        player.gameMode = gameMode
        player.setHeldItemSlot(heldSlot)
        player.exp = exp
        loadAllItems(inventory, player.inventory)
    }

}


