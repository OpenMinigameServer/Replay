package io.github.openminigameserver.replay.replayer.statehelper.utils

import net.minestom.server.entity.Player
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import net.minestom.server.registry.Registries
import net.minestom.server.utils.NBTUtils
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTTypes

internal fun getPlayerInventoryCopy(player: Player) = NBTList<NBTCompound>(NBTTypes.TAG_Compound).also {
    saveAllItems(
        it,
        player.inventory
    )
}

internal fun loadAllItems(items: NBTList<NBTCompound>, destination: PlayerInventory) {
    destination.clear()
    for (tag in items) {
        val item = Registries.getMaterial(tag.getString("id"))
        val stack = ItemStack(item, tag.getByte("Count")!!)
        if (tag.containsKey("tag")) {
            NBTUtils.loadDataIntoItem(stack, tag.getCompound("tag")!!)
        }
        destination.setItemStack(tag.getByte("Slot")!!.toInt(), stack)
    }
    destination.update()
}

internal fun saveAllItems(list: NBTList<NBTCompound>, inventory: PlayerInventory) {
    for (i in 0 until inventory.size) {
        val stack = inventory.getItemStack(i)
        val nbt = NBTCompound()
        val tag = NBTCompound()
        NBTUtils.saveDataIntoNBT(stack, tag)
        nbt["tag"] = tag
        nbt.setByte("Slot", i.toByte())
        nbt.setByte("Count", stack.amount)
        nbt.setString("id", stack.material.getName())
        list.add(nbt)
    }
}