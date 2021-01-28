package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.extensions.runOnSeparateThread
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.chat.ChatColor
import net.minestom.server.entity.Player
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration

object ReplayCommand {
    @CommandMethod("replay play <id>")
    fun playReplay(sender: Player, @Argument("id", suggestions = "replay") id: UUID) {
        val instance = sender.instance!!
        if (instance.replaySession != null) {
            sender.sendMessage(ChatColor.RED.toString() + "You are already in a replay session.")
            return
        }
        runOnSeparateThread {
            try {

                sender.sendMessage(ChatColor.GRAY.toString() + "Attempting to load replay...")

                val replay = ReplayManager.storageSystem.loadReplay(id)

                if (replay == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a valid Replay ID!")
                    return@runOnSeparateThread
                }

                val session = ReplaySession(
                    instance,
                    replay,
                    instance.players.toMutableList(),
                    TickTime(1, TimeUnit.MILLISECOND)
                )
                instance.replaySession = session
                session.init()

            } catch (e: Throwable) {
                e.printStackTrace()
                sender.sendMessage(ChatColor.RED.toString() + "An error occurred while trying to load your replay.")
                return@runOnSeparateThread
            }
        }
    }

    @CommandMethod("replay pause|start|resume")
    fun pauseReplay(sender: Player) {
        val session = sender.instance!!.replaySession ?: return

        session.paused = !session.paused
    }

    @CommandMethod("replay exit")
    fun exitReplay(sender: Player) {
        val session = sender.instance!!.replaySession ?: return

        session.removeViewer(sender)
    }

    @CommandMethod("replay restart")
    fun restartReplay(sender: Player) {
        val session = sender.instance!!.replaySession ?: return

        session.time = Duration.ZERO
        session.paused = false
    }

    @CommandMethod("replay dump <id>")
    fun dumpReplay(sender: Player, @Argument("id", suggestions = "replay") id: UUID) {
        runOnSeparateThread {
            try {
                sender.sendMessage(ChatColor.GRAY.toString() + "Attempting to load replay...")

                val replay = ReplayManager.storageSystem.loadReplay(id)

                if (replay == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a valid Replay ID!")
                    return@runOnSeparateThread
                }

                //TODO: sender.sendMessage(ReplayFile.dumpReplayToString(replay))
            } catch (e: Throwable) {
                e.printStackTrace()
                sender.sendMessage(ChatColor.RED.toString() + "An error occurred while trying to load your replay.")
                return@runOnSeparateThread
            }
        }

    }
}