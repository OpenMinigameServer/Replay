package io.github.openminigameserver.replay

import com.destroystokyo.paper.profile.ProfileProperty
import io.github.openminigameserver.replay.abstraction.ReplayActionItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.Vector
import java.util.*

fun RecordablePosition.toBukkit(world: World): Location = Location(world, x, y, z, yaw, pitch)
fun Location.toReplay(): RecordablePosition = RecordablePosition(x, y, z, yaw, pitch)

fun RecordableVector.toBukkit(): Vector = Vector(x, y, z)
fun Vector.toReplay(): RecordableVector = RecordableVector(x, y, z)

fun ReplayActionItemStack.toBukkit(): ItemStack {
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
            itemMeta = (Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD) as SkullMeta).apply {
                this.playerProfile = Bukkit.createProfile(UUID.randomUUID()).apply {
                    this.setProperty(ProfileProperty("textures", itemSkin.value, itemSkin.signature))
                }
            }
        }

        itemMeta = itemMeta.apply {
            displayName(Component.text("§r").append(title))
        }
//        controlItemAction = action
    }
}

fun runSync(code: () -> Unit) {
    Bukkit.getScheduler().runTask(ReplayPlugin.instance, Runnable(code))
}