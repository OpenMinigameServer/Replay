package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.platform.ReplayExtension
import io.github.openminigameserver.replay.runOnSeparateThread
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*

object StopRecordingCommand {

    @CommandMethod("stoprecording")
    fun stopRecording(sender: ReplayUser, extension: ReplayExtension) {
        val recorder = sender.world?.recorder ?: let {
            sender.sendMessage(text("Please start a recording.", RED))
            return
        }
        sender.world?.recorder = null

        recorder.stopRecording()
        sender.sendMessage(text("Recording stopped.", GREEN))

        runOnSeparateThread {
            extension.replayManager.storageSystem.saveReplay(recorder.replay)
            val message = text {

                recorder.replay.actions.groupBy { it.javaClass.simpleName }
                    .forEach { actions -> it.append(text("${actions.key}: ${actions.value.count()}", GOLD)) }
            }
            sender.sendMessage(
                text("Your replay has been created! Click here to play it!", GOLD).hoverEvent(message)
            )
        }
    }
}
