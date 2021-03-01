package io.github.openminigameserver.replay.player.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecParticleEffect
import io.github.openminigameserver.replay.player.ActionPlayer
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.utils.PacketUtils
import java.util.function.Consumer

object RecParticleEffectPlayer : ActionPlayer<RecParticleEffect> {
    override fun play(action: RecParticleEffect, session: ReplaySession, instance: Instance, viewers: List<Player>) {
        PacketUtils.sendGroupedPacket(viewers, ParticlePacket().apply {
            this.particleId = action.particleId
            this.particleCount = action.particleCount
            this.particleData = action.particleData
            this.x = action.x
            this.y = action.y
            this.z = action.z
            this.offsetX = action.offsetX
            this.offsetY = action.offsetY
            this.offsetZ = action.offsetZ
            this.longDistance = action.longDistance
            this.dataConsumer = action.extraData?.let { bytes -> Consumer { it.writeBytes(bytes) } }
        })
    }
}