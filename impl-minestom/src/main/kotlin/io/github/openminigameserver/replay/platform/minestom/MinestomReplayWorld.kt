package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.ReplayChunk
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import net.minestom.server.instance.Instance
import java.util.*

class MinestomReplayWorld(val instance: Instance) : ReplayWorld() {
    override val uuid: UUID
        get() = instance.uniqueId
    override val entities: Iterable<ReplayEntity>
        get() = TODO("Not yet implemented")
    override val chunks: Iterable<ReplayChunk>
        get() = TODO("Not yet implemented")

}