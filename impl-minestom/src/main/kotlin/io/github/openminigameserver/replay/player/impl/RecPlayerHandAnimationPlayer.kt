package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
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
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        if (nativeEntity !is Player) return
        when (action.hand) {
            Hand.MAIN -> nativeEntity.swingMainHand()
            Hand.OFF -> nativeEntity.swingOffHand()
        }
    }
}