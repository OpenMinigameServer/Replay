package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import io.github.openminigameserver.cloudminestom.MinestomCommandManager
import io.github.openminigameserver.replay.abstraction.ReplayUser
import net.minestom.server.entity.Player
import java.util.*
import java.util.function.Function

val replayUsers = mutableMapOf<UUID, ReplayUser>()

fun getReplayUser(player: Player): ReplayUser {
    return replayUsers.getOrPut(player.uuid) { MinestomReplayUser(player) }
}

class ReplayCommandManager : MinestomCommandManager<ReplayUser>(
    AsynchronousCommandExecutionCoordinator.newBuilder<ReplayUser>().withAsynchronousParsing().build(),
    Function {
        getReplayUser(it.asPlayer())
    }, Function {
        (it as MinestomReplayUser).player
    }
)