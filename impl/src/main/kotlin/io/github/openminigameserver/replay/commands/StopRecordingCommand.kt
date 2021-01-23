package io.github.openminigameserver.replay.commands

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.extensions.recorder
import io.github.openminigameserver.replay.extensions.runOnSeparateThread
import net.minestom.server.chat.*
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

object StopRecordingCommand : Command("stoprecording") {
    init {
        setDefaultExecutor { sender, args ->
            if (sender !is Player) return@setDefaultExecutor

            val recorder = sender.recorder ?: let {
                sender.sendMessage(ChatColor.RED.toString() + "Please start a recording.")
                return@setDefaultExecutor
            }
            sender.recorder = null

            recorder.stopRecording()
            sender.sendMessage(ChatColor.BRIGHT_GREEN.toString() + "Recording stopped.")

            runOnSeparateThread {
                ReplayManager.storageSystem.saveReplay(recorder.replay)
                val message = recorder.replay.actions.groupBy { it.javaClass.simpleName }
                    .map { ChatColor.GOLD.toString() + "${it.key}: ${it.value.count()}" }.joinToString("\n")
                sender.sendMessage(
                    RichMessage.of(
                        ColoredText.of(
                            ChatColor.GOLD,
                            "Your replay has been created! Click here to play it!"
                        )
                    ).setClickEvent(
                        ChatClickEvent.runCommand("/replay play ${recorder.replay.id}")
                    )
                        .setHoverEvent(ChatHoverEvent.showText(ColoredText.of(message)))
                )
            }

        }
    }
}