package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.extensions.*
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMetadata
import io.github.openminigameserver.replay.model.recordable.impl.RecPlayerHandAnimation
import io.github.openminigameserver.replay.player.inventory.ControlItemAction
import io.github.openminigameserver.replay.player.inventory.controlItemAction
import net.minestom.server.MinecraftServer
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket

object ReplayListener {

    fun registerListener() {

        MinecraftServer.getGlobalEventHandler().addEventCallback(PlayerUseItemEvent::class.java) {
            val replaySession = it.player.instance!!.replaySession ?: return@addEventCallback

            val action = it.itemStack.controlItemAction
            if (action != ControlItemAction.NONE) {
                runOnSeparateThread {
                    replaySession.playerStateHelper.handleItemAction(it.player, action)
                }
            }

        }

        MinecraftServer.getGlobalEventHandler().addEventCallback(InventoryPreClickEvent::class.java) {
            it.player.instance!!.replaySession ?: return@addEventCallback

            if (it.inventory == null)
                it.isCancelled = true
        }

        registerHandAnimation()
        registerPacketListener()
    }

    private fun registerHandAnimation() {
        MinecraftServer.getGlobalEventHandler().addEventCallback(PlayerHandAnimationEvent::class.java) {
            val replay = it.player.instance?.recorder?.replay ?: return@addEventCallback

            replay.addAction(RecPlayerHandAnimation(enumValueOf(it.hand.name), replay.getEntity(it.player)))
        }
    }

    private fun registerPacketListener() {
        MinecraftServer.getConnectionManager().onPacketSend { players, packetController, packet ->
            if (packet is EntityMetaDataPacket) {
                val player = players.firstOrNull { it.entityId == packet.entityId } ?: return@onPacketSend
                val replay = player.instance?.recorder?.replay ?: return@onPacketSend

                val metadataArray = packet.getMetadataArray()

                replay.addAction(RecEntityMetadata(metadataArray, replay.getEntityById(packet.entityId)))
            }
        }
    }

}
