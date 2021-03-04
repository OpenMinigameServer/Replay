package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.TimeUnit
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.runOnSeparateThread
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import kotlin.time.Duration

object ReplayCommand {
    @CommandMethod("replay play <id>")
    fun playReplay(sender: ReplayUser, @Argument("id", suggestions = "replay") id: UUID, manager: ReplayManager) {
        var instance = sender.world ?: return
        if (instance.replaySession != null) {
            sender.sendMessage(text("You are already in a replay session.", NamedTextColor.RED))
            return
        }
        runOnSeparateThread {
            try {
                sender.sendMessage(text("Attempting to load replay...", NamedTextColor.GRAY))

                val replay = manager.storageSystem.loadReplay(id)

                if (replay == null) {
                    sender.sendMessage(text("Please provide a valid Replay ID!", NamedTextColor.RED))
                    return@runOnSeparateThread
                }

                val session = manager.extension.platform.createReplaySession(
                    replay,
                    instance.entities.filterIsInstance<ReplayUser>().toMutableList(),
                    instance,
                    TickTime(1, TimeUnit.MILLISECOND)
                )
                instance = session.world
                instance.replaySession = session
                session.init()

            } catch (e: Throwable) {
                e.printStackTrace()
                sender.sendMessage(text("An error occurred while trying to load your replay.", NamedTextColor.RED))
                return@runOnSeparateThread
            }
        }
    }

    @CommandMethod("replay pause|start|resume")
    fun pauseReplay(sender: ReplayUser) {
        val session = sender.instance?.replaySession ?: return

        session.paused = !session.paused
    }

    @CommandMethod("replay exit")
    fun exitReplay(sender: ReplayUser) {
        val session = sender.instance?.replaySession ?: return

        session.removeViewer(sender)
    }

    @CommandMethod("replay restart")
    fun restartReplay(sender: ReplayUser) {
        val session = sender.instance?.replaySession ?: return

        session.time = Duration.ZERO
        session.paused = false
    }
}