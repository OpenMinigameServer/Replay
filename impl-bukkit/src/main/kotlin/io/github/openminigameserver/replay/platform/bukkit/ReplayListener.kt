package io.github.openminigameserver.replay.platform.bukkit

import io.github.openminigameserver.replay.ReplayPlugin
import io.github.openminigameserver.replay.replaySession
import io.github.openminigameserver.replay.replayUser
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import io.github.openminigameserver.replay.toReplayAction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object ReplayListener : Listener {

    val platform by lazy {
        ReplayPlugin.platform
    }

    @EventHandler
    fun onHandSwing(e: PlayerInteractEvent) {
        val heldItem = e.item ?: return
        val replaySession = e.player.world.replaySession ?: return
        val replayPlayer = e.player.replayUser
        val itemStack = heldItem.toReplayAction().takeIf { it.action != ControlItemAction.NONE } ?: return

        e.isCancelled = true
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            replaySession.playerStateHelper.handleItemSwing(replayPlayer, itemStack)
        } else if ((e.action == Action.RIGHT_CLICK_AIR || (e.action == Action.RIGHT_CLICK_BLOCK && e.interactionPoint != null))) {
            replaySession.playerStateHelper.handleItemAction(replayPlayer, itemStack.action)
        }
    }

}