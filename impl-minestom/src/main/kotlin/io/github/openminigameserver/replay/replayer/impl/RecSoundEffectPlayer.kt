package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.model.recordable.impl.RecSoundEffect
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.SoundEffectPacket
import net.minestom.server.sound.SoundCategory
import net.minestom.server.utils.PacketUtils

object RecSoundEffectPlayer : MinestomActionPlayer<RecSoundEffect> {
    override fun play(
        @Suppress("DuplicatedCode") action: RecSoundEffect,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        PacketUtils.sendGroupedPacket(viewers, SoundEffectPacket().apply {
            this.soundId = action.soundId
            this.soundCategory = action.soundCategory?.let { SoundCategory.valueOf(it.name) }
            this.pitch = action.pitch
            this.volume = action.volume
            this.x = action.x
            this.y = action.y
            this.z = action.z
        })
    }
}