package io.github.openminigameserver.replay.player.statehelper.constants

import io.github.openminigameserver.replay.player.statehelper.ControlItemAction
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.PlayerHeadMeta
import java.util.*
import kotlin.time.Duration

const val actionData = "controlItem"

var ItemStack.controlItemAction: ControlItemAction
    get() = data?.get<ControlItemAction>(actionData) ?: ControlItemAction.NONE
    set(value) {
        if (data == null) {
            data = DataImpl()
        }
        data?.set(actionData, value)
    }

object PlayerHeadsItems {
    fun getPlayPauseItem(paused: Boolean, finished: Boolean): ItemStack {
        if (finished) {
            return ItemStack(Material.LIME_DYE, 1).apply {
                displayName = ColoredText.of(ChatColor.BRIGHT_GREEN, "Play Recording Again")
                controlItemAction = ControlItemAction.PLAY_AGAIN
            }
        }
        return ItemStack(if (paused) Material.MAGENTA_DYE else Material.GRAY_DYE, 1).apply {
            displayName = ColoredText.of(ChatColor.BRIGHT_GREEN, "Click to ${if (paused) "Resume" else "Pause"}")
            controlItemAction = if (paused) ControlItemAction.RESUME else ControlItemAction.PAUSE
        }
    }

    fun getDecreaseSpeedItem() =
        buildItemStack(PlayerHeadsTextureData.decreaseSpeed, "Decrease Speed", ControlItemAction.COOL_DOWN)

    fun getIncreaseSpeedItem() =
        buildItemStack(PlayerHeadsTextureData.increaseSpeed, "Increase Speed", ControlItemAction.SPEED_UP)

    private fun buildItemStack(skin: PlayerSkin, name: String, action: ControlItemAction) =
        ItemStack(Material.PLAYER_HEAD, 1).apply {
            itemMeta = getHeadMeta(skin)
            displayName = ColoredText.of(ChatColor.BRIGHT_GREEN, name)
            controlItemAction = action
        }

    private fun getHeadMeta(skin: PlayerSkin) = PlayerHeadMeta().apply {
        setPlayerSkin(skin)
        setSkullOwner(UUID.randomUUID())
    }

    fun getStepBackwardsItem(skipSpeed: Duration): ItemStack = buildItemStack(
        PlayerHeadsTextureData.backwards,
        "${skipSpeed.inSeconds.toInt()}s Backwards",
        ControlItemAction.STEP_BACKWARDS
    )

    fun getStepForwardItem(skipSpeed: Duration): ItemStack = buildItemStack(
        PlayerHeadsTextureData.forwards,
        "${skipSpeed.inSeconds.toInt()}s Forward",
        ControlItemAction.STEP_FORWARD
    )

}