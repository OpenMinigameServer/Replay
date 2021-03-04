package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.recorder.RecorderOptions
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

object StartRecordingCommand {

    @CommandMethod("startrecordingchunks")
    fun startRecordingWithChunks(sender: ReplayUser, replayManager: ReplayManager) {
        startRecordingReplay(sender, true, replayManager)
    }

    @CommandMethod("startrecording")
    fun startRecording(sender: ReplayUser, replayManager: ReplayManager) {
        startRecordingReplay(sender, false, replayManager)
    }

    private fun startRecordingReplay(sender: ReplayUser, recordChunks: Boolean = false, replayManager: ReplayManager) {
        sender.sendMessage(Component.text("Recording started.", NamedTextColor.GREEN))
        sender.sendMessage(
            text("Click here to stop recording.").clickEvent(ClickEvent.runCommand("/stoprecording"))
        )
        val recorder = ReplayRecorder(
            replayManager.extension,
            sender.instance!!,
            RecorderOptions(recordInstanceChunks = recordChunks)
        )
        sender.instance!!.recorder = recorder

        recorder.startRecording()
    }
}