package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.impl.RecBlockEffect
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.EffectPacket
import net.minestom.server.utils.PacketUtils

object RecBlockEffectPlayer : MinestomActionPlayer<RecBlockEffect> {
    override fun play(action: RecBlockEffect, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        PacketUtils.sendGroupedPacket(viewers, EffectPacket().apply {
            effectId = action.effectId
            this.data = action.data
            this.position = action.position.toMinestom().toBlockPosition()
            this.disableRelativeVolume = action.disableRelativeVolume
        })
    }
}