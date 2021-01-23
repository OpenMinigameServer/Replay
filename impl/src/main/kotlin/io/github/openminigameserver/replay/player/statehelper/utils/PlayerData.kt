package io.github.openminigameserver.replay.player.statehelper.utils

import net.minestom.server.entity.Player
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList

class PlayerData(private val heldSlot: Byte, private val exp: Float, private val inventory: NBTList<NBTCompound>) {

    constructor(player: Player) : this(player.heldSlot, player.exp, getPlayerInventoryCopy(player))

    fun apply(player: Player) {
        player.setHeldItemSlot(heldSlot)
        player.exp = exp
        loadAllItems(inventory, player.inventory)
    }

}


