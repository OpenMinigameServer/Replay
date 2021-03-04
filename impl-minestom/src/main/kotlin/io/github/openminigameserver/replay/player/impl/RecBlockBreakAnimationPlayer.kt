package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecBlockBreakAnimation
import io.github.openminigameserver.replay.replayer.EntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket
import net.minestom.server.utils.PacketUtils

object RecBlockBreakAnimationPlayer : EntityActionPlayer<RecBlockBreakAnimation>() {
    override fun play(
        action: RecBlockBreakAnimation,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        PacketUtils.sendGroupedPacket(
            viewers,
            BlockBreakAnimationPacket(
                nativeEntity.entityId,
                action.position.toMinestom().toBlockPosition(),
                action.destroyStage
            )
        )
    }
}