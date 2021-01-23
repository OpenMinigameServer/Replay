package io.github.openminigameserver.replay.commands

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.extensions.runOnSeparateThread
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.chat.ChatColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.entity.Player
import java.util.*
import kotlin.time.Duration

object ReplayCommand : Command("replay") {
    init {
        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val id = kotlin.runCatching { UUID.fromString(args.getString("id")) }.getOrNull() ?: let {
                sender.sendMessage(ChatColor.RED.toString() + "Please provide a valid Replay ID!")
                return@addSyntax
            }

            val instance = sender.instance!!
            if (instance.replaySession != null) {
                sender.sendMessage(ChatColor.RED.toString() + "You are already in a replay session already.")
                return@addSyntax
            }
            runOnSeparateThread {
                val replay = ReplayManager.storageSystem.loadReplay(id)

                if (replay == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a valid Replay ID!")
                    return@runOnSeparateThread
                }


                val session = ReplaySession(instance, replay, mutableListOf(sender))
                instance.replaySession = session
                session.init()

            }

        }, ArgumentWord("action").from("play"), ArgumentWord("id"))
        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val session = sender.instance!!.replaySession ?: return@addSyntax

            session.paused = !session.paused

        }, ArgumentWord("action").from("play", "pause", "start"))

        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val session = sender.instance!!.replaySession ?: return@addSyntax

            session.removeViewer(sender)

        }, ArgumentWord("action").from("exit"))

        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val session = sender.instance!!.replaySession ?: return@addSyntax

            session.time = Duration.ZERO
            session.paused = false

        }, ArgumentWord("action").from("restart"))
    }
}