package io.github.openminigameserver.replay.platform.bukkit

import io.github.openminigameserver.replay.ReplayPlugin
import io.github.openminigameserver.replay.abstraction.ReplayChunk
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import org.bukkit.World
import java.util.*

class BukkitReplayWorld(val world: World) : ReplayWorld() {
    override val uuid: UUID
        get() = world.uid
    override val entities: Iterable<ReplayEntity>
        get() = world.entities.map { ReplayPlugin.platform.entities.getOrCompute(it.entityId) }
    override val chunks: Iterable<ReplayChunk>
        get() = TODO("Not yet implemented")
}