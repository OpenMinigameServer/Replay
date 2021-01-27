package io.github.openminigameserver.replay.commands

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import io.github.openminigameserver.cloudminestom.MinestomCommandManager
import io.github.openminigameserver.replay.ReplayManager
import kotlinx.coroutines.runBlocking
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import java.util.function.Function.identity

class ReplayCommandManager : MinestomCommandManager<CommandSender>(
    AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().withAsynchronousParsing().build(),
    identity(), identity()
) {
    init {
        parserRegistry.registerSuggestionProvider("replay") { t, u ->
            if (t.sender is Player)
                runBlocking { ReplayManager.storageSystem.getReplaysForPlayer((t.sender as Player).uuid) }.map { it.toString() }
            else mutableListOf()
        }
    }

    val annotationParser = AnnotationParser(this, CommandSender::class.java) { createDefaultCommandMeta() }
}