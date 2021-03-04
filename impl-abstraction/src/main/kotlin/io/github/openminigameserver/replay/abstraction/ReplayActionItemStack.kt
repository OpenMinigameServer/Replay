package io.github.openminigameserver.replay.abstraction

import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.Component

data class ReplayActionItemStack(
    val title: Component,
    val action: ControlItemAction,
    val skin: ReplayHeadTextureSkin? = null
) {
    companion object {
        val air = ReplayActionItemStack(Component.empty(), action = ControlItemAction.NONE)
    }
}