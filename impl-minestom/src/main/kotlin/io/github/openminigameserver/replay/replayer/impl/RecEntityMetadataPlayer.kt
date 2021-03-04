package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMetadata
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomEntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.utils.PacketUtils
import net.minestom.server.utils.binary.BinaryWriter

object RecEntityMetadataPlayer : MinestomEntityActionPlayer<RecEntityMetadata>() {
    override fun play(
        action: RecEntityMetadata,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        PacketUtils.sendGroupedPacket(viewers, object : EntityMetaDataPacket() {
            override fun write(writer: BinaryWriter) {
                writer.writeVarInt(nativeEntity.entityId)
                writer.write(action.metadata)
                writer.writeByte(0xFF.toByte())
            }
        })
    }
}