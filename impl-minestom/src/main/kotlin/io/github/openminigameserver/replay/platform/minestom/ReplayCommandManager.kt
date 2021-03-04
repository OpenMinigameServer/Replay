package io.github.openminigameserver.replay.platform.minestom

import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import io.github.openminigameserver.cloudminestom.MinestomCommandManager
import io.github.openminigameserver.replay.abstraction.ReplayUser
import java.util.function.Function

class ReplayCommandManager(replayPlatform: MinestomReplayPlatform) : MinestomCommandManager<ReplayUser>(
    AsynchronousCommandExecutionCoordinator.newBuilder<ReplayUser>().withAsynchronousParsing().build(),
    Function {
        replayPlatform.entities.getOrCompute(it.asPlayer().entityId) as ReplayUser
    }, Function {
        (it as MinestomReplayUser).player
    }
)