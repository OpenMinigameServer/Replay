package io.github.openminigameserver.replay

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import io.github.openminigameserver.replay.abstraction.ReplayActionItemStack
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayHeadTextureSkin
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerSkinData
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayEntity
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayUser
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayWorld
import io.github.openminigameserver.replay.replayer.ReplaySession
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.*
import java.util.Base64.getDecoder

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

        if (itemMeta != null) {
            itemMeta = itemMeta.apply {
                displayName(text("§r").append(title))
            }

            controlItemAction = action
        }
    }
}

internal fun PlayerProfile.toReplay(): PlayerSkinData? {
    val decoder = getDecoder()
    val property = properties.firstOrNull() ?: return null
    return PlayerSkinData(decoder.decode(property.value), decoder.decode(property.signature))
}


fun ItemStack.toReplayAction(): ReplayActionItemStack {
    val itemMeta = itemMeta
    return ReplayActionItemStack(
        itemMeta.displayName() ?: empty(),
        controlItemAction ?: ControlItemAction.NONE,
        itemMeta?.takeIf { it is SkullMeta }?.let {
            it as SkullMeta
            val value = it.playerProfile?.properties?.first()
            ReplayHeadTextureSkin(value?.value ?: "", value?.signature ?: "")
        }
    )
}

val controlItemActionTag = NamespacedKey(ReplayPlugin.instance, "control_item_action")

var ItemStack.controlItemAction: ControlItemAction?
    get() {
        if (!hasItemMeta()) return null
        return itemMeta.persistentDataContainer.get(controlItemActionTag, PersistentDataType.STRING)
            ?.let { ControlItemAction.valueOf(it) }
    }
    set(value) {
        itemMeta = itemMeta.apply {
            if (value != null) {
                persistentDataContainer.set(controlItemActionTag, PersistentDataType.STRING, value.name)
            } else {
                persistentDataContainer.remove(controlItemActionTag)
            }
        }
    }

fun runSync(code: () -> Unit) {
    if (!Bukkit.getServer().isPrimaryThread) {
        Bukkit.getScheduler().runTask(ReplayPlugin.instance, Runnable(code))
    } else {
        code()
    }
}

val World.replayWorld: BukkitReplayWorld
    get() = ReplayPlugin.platform.getWorldById(uid)

var World.replaySession: ReplaySession?
    get() = replayWorld.replaySession
    set(value) {
        replayWorld.replaySession = value
    }
val Entity.replayEntity: ReplayEntity
    get() = ReplayPlugin.platform.entities.getOrCompute(entityId)

val Entity.bukkitReplayEntity: BukkitReplayEntity
    get() = replayEntity as BukkitReplayEntity

val Player.replayUser: BukkitReplayUser
    get() = replayEntity as BukkitReplayUser