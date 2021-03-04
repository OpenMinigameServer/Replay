package io.github.openminigameserver.replay.replayer.statehelper.constants

import io.github.openminigameserver.replay.abstraction.ReplayActionItemStack
import io.github.openminigameserver.replay.abstraction.ReplayHeadTextureSkin
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.time.Duration

const val actionData = "controlItem"

object PlayerHeadsItems {
    fun getPlayPauseItem(paused: Boolean, finished: Boolean): ReplayActionItemStack {
        if (finished) {
            return ReplayActionItemStack(
                text("Play Recording Again", NamedTextColor.GREEN),
                ControlItemAction.PLAY_AGAIN
            )
        }
        return ReplayActionItemStack(
            text("Click to ${if (paused) "Resume" else "Pause"}", NamedTextColor.GREEN),
            if (paused) ControlItemAction.RESUME else ControlItemAction.PAUSE
        )
    }

    fun getDecreaseSpeedItem() =
        buildItemStack(PlayerHeadsTextureData.decreaseSpeed, "Decrease Speed", ControlItemAction.COOL_DOWN)

    fun getIncreaseSpeedItem() =
        buildItemStack(PlayerHeadsTextureData.increaseSpeed, "Increase Speed", ControlItemAction.SPEED_UP)

    private fun buildItemStack(skin: ReplayHeadTextureSkin, name: String, action: ControlItemAction) =
        ReplayActionItemStack(text(name, NamedTextColor.GREEN), action, skin)

    fun getStepBackwardsItem(skipSpeed: Duration): ReplayActionItemStack = buildItemStack(
        PlayerHeadsTextureData.backwards,
        "${skipSpeed.inSeconds.toInt()}s Backwards",
        ControlItemAction.STEP_BACKWARDS
    )

    fun getStepForwardItem(skipSpeed: Duration): ReplayActionItemStack = buildItemStack(
        PlayerHeadsTextureData.forwards,
        "${skipSpeed.inSeconds.toInt()}s Forward",
        ControlItemAction.STEP_FORWARD
    )

}