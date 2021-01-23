package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.model.recordable.impl.Hand
import io.github.openminigameserver.replay.model.recordable.impl.RecPlayerHandAnimation
import io.github.openminigameserver.replay.player.EntityActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

object RecPlayerHandAnimationPlayer : EntityActionPlayer<RecPlayerHandAnimation>() {
    override fun play(
        action: RecPlayerHandAnimation,
        entity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        if (entity !is Player) return
        when (action.hand) {
            Hand.MAIN -> entity.swingMainHand()
            Hand.OFF -> entity.swingOffHand()
        }
    }
}