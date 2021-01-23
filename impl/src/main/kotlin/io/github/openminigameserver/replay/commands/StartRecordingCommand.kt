package io.github.openminigameserver.replay.commands

import io.github.openminigameserver.replay.extensions.recorder
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import net.minestom.server.chat.ChatClickEvent
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.chat.RichMessage
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

object StartRecordingCommand : Command("startrecording") {
    init {
        setDefaultExecutor { sender, args ->
            if (sender !is Player) return@setDefaultExecutor

            sender.sendMessage(ChatColor.BRIGHT_GREEN.toString() + "Recording started.")
            sender.sendMessage(RichMessage.of(ColoredText.of(ChatColor.GOLD, "Click here to stop recording.")).setClickEvent(
                ChatClickEvent.runCommand("/stoprecording")))
            val recorder = ReplayRecorder(sender.instance!!)
            sender.recorder = recorder

            recorder.startRecording()
        }
    }
}