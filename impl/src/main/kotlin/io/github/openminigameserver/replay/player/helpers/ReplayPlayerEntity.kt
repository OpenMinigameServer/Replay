package io.github.openminigameserver.replay.player.helpers

import io.netty.channel.local.LocalAddress
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.player.PlayerConnection
import net.minestom.server.utils.binary.BinaryWriter
import java.net.SocketAddress
import java.util.*
import java.util.function.Consumer

class ReplayPlayerEntity(uuid: UUID, username: String, private val firstMetadata: ByteArray) :
    Player(uuid, "$username§r".take(16), ReplayPlayerConnection) {
    init {
        settings.refresh(Locale.ENGLISH.toLanguageTag(), 0, ChatMode.ENABLED, true, 127, MainHand.RIGHT)
    }

    private var isFirstMetadata = true
    override fun getMetadataConsumer(): Consumer<BinaryWriter> {
        if (isFirstMetadata) {
            isFirstMetadata = false
            return Consumer { it.write(firstMetadata) }
        }
        return super.getMetadataConsumer()
    }
}


object ReplayPlayerConnection : PlayerConnection() {

    override fun sendPacket(serverPacket: ServerPacket) {
    }

    /**
     * Gets the remote address of the client.
     *
     * @return the remote address
     */
    override fun getRemoteAddress(): SocketAddress {
        return LocalAddress("replay-player")
    }

    /**
     * Forcing the player to disconnect.
     */
    override fun disconnect() {
    }

}
