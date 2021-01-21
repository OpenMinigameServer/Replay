package io.github.openminigameserver.replay.player.helpers

import io.netty.channel.local.LocalAddress
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.player.PlayerConnection
import java.net.SocketAddress
import java.util.*

class ReplayPlayerEntity(uuid: UUID, username: String) : Player(uuid, username, ReplayPlayerConnection) {
    init {
        settings.refresh(Locale.ENGLISH.toLanguageTag(), 0, ChatMode.ENABLED, true,  127, MainHand.RIGHT)
    }
}


object ReplayPlayerConnection : PlayerConnection() {
    /**
     * Serializes the packet and send it to the client.
     *
     *
     * Also responsible for executing [ConnectionManager.onPacketSend] consumers.
     *
     * @param serverPacket the packet to send
     * @see .shouldSendPacket
     */
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
