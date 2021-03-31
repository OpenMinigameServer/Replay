package io.github.openminigameserver.replay.abstraction

import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

data class ReplayActionItemStack(
    var title: Component,
    val action: ControlItemAction,
    val skin: ReplayHeadTextureSkin? = null
) {
    init {
        title = title.decoration(TextDecoration.ITALIC, false)
    }

    companion object {
        val air = ReplayActionItemStack(Component.empty(), action = ControlItemAction.NONE)
    }
}