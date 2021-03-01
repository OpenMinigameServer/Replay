package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.replay.extensions.recorder
import io.github.openminigameserver.replay.recorder.RecorderOptions
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import net.minestom.server.chat.ChatClickEvent
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.chat.RichMessage
import net.minestom.server.entity.Player

object StartRecordingCommand {

    @CommandMethod("startrecordingchunks")
    fun startRecordingWithChunks(sender: Player) {
        startRecordingReplay(sender, true)
    }

    @CommandMethod("startrecording")
    fun startRecording(sender: Player) {
        startRecordingReplay(sender)
    }

    private fun startRecordingReplay(sender: Player, recordChunks: Boolean = false) {
        sender.sendMessage(ChatColor.BRIGHT_GREEN.toString() + "Recording started.")
        sender.sendMessage(
            RichMessage.of(ColoredText.of(ChatColor.GOLD, "Click here to stop recording.")).setClickEvent(
                ChatClickEvent.runCommand("/stoprecording")
            )
        )
        val recorder = ReplayRecorder(
            sender.instance!!,
            RecorderOptions(recordInstanceChunks = recordChunks)
        )
        sender.recorder = recorder

        recorder.startRecording()
    }
}