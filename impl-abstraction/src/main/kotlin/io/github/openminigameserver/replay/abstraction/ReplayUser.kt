package io.github.openminigameserver.replay.abstraction

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience

/**
 * Represents a Player that will use the Replay System
 */
abstract class ReplayUser : ForwardingAudience, ReplayEntity {
    abstract val audience: Audience
    abstract val name: String

    override fun audiences(): Iterable<Audience> {
        return listOf(audience)
    }

    abstract fun setWorld(instance: ReplayWorld)
    abstract fun clearInventory()
    abstract fun setHeldItemSlot(count: Byte)
}