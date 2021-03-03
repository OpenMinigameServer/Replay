package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.minestom.MinestomAudiences
import net.minestom.server.entity.Player
import java.util.*

internal val audiences = MinestomAudiences.create()

class MinestomReplayUser(val player: Player) : ReplayUser() {
    override val audience: Audience = audiences.player(player)

    override val name: String
        get() = player.username
    override val uuid: UUID
        get() = player.uuid
    override val world: ReplayWorld
        get() = TODO("Not yet implemented")
}