package io.github.openminigameserver.replay.helpers

import io.netty.channel.local.LocalAddress
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.network.player.PlayerConnection
import net.minestom.server.utils.binary.BinaryWriter
import java.net.SocketAddress
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class ReplayPlayerEntity(uuid: UUID, username: String, private val firstMetadata: ByteArray) :
    Player(uuid, "$username§r".take(16), ReplayPlayerConnection()) {
    init {
        settings.refresh(Locale.ENGLISH.toLanguageTag(), 0, ChatMode.ENABLED, true, 127, MainHand.RIGHT)
    }

    private var isFirstMetadata = true

    override fun getMetadataPacket(): EntityMetaDataPacket {
        if (isFirstMetadata) {
            isFirstMetadata = false
            return object : EntityMetaDataPacket() {
                override fun write(writer: BinaryWriter) {
                    writer.writeVarInt(this@ReplayPlayerEntity.entityId)
                    writer.write(firstMetadata)
                    writer.writeByte(0xFF.toByte())
                }
            }
        }
        return super.getMetadataPacket()
    }
}


class ReplayPlayerConnection : PlayerConnection() {
    private val id = counter.getAndIncrement()

    companion object {
        private val counter = AtomicInteger()
    }

    override fun sendPacket(serverPacket: ServerPacket) {
    }

    /**
     * Gets the remote address of the client.
     *
     * @return the remote address
     */
    override fun getRemoteAddress(): SocketAddress {
        return LocalAddress("replay-player$id")
    }

    /**
     * Forcing the player to disconnect.
     */
    override fun disconnect() {
    }

}
