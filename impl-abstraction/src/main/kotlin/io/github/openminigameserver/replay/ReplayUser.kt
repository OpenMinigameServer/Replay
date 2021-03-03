package io.github.openminigameserver.replay

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import java.util.*

/**
 * Represents a Player that will use the Replay System
 */
abstract class ReplayUser : ForwardingAudience {
    abstract val audience: Audience
    abstract val name: String
    abstract val uuid: UUID
    abstract val world: ReplayWorld
    val instance: ReplayWorld get() = world

    override fun audiences(): Iterable<Audience> {
        return listOf(audience)
    }
}