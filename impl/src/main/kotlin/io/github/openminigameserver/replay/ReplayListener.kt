package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.extensions.*
import io.github.openminigameserver.replay.helpers.ReplayPlayerEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMetadata
import io.github.openminigameserver.replay.model.recordable.impl.RecPlayerHandAnimation
import io.github.openminigameserver.replay.player.statehelper.ControlItemAction
import io.github.openminigameserver.replay.player.statehelper.constants.controlItemAction
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket

object ReplayListener {

    private val playerDisconnectHandler: (event: PlayerDisconnectEvent) -> Unit = {
        if (it.player !is FakePlayer && it.player !is ReplayPlayerEntity)
            it.player.instance!!.replaySession?.removeViewer(it.player)
    }

    private val playerSwapItemEvent: (event: PlayerSwapItemEvent) -> Unit = {
        if (it.player.instance!!.replaySession != null) {
            it.isCancelled = true
        }
    }

    private val useItemHandler: (event: PlayerUseItemEvent) -> Unit = eventCallback@{
        val replaySession = it.player.instance!!.replaySession ?: return@eventCallback

        val action = it.itemStack.controlItemAction
        if (action != ControlItemAction.NONE) {
            runOnSeparateThread {
                replaySession.playerStateHelper.handleItemAction(it.player, action)
            }
        }
    }

    private val inventoryPreClickHandler: (event: InventoryPreClickEvent) -> Unit = eventCallback@{
        it.player.instance!!.replaySession ?: return@eventCallback

        if (it.inventory == null)
            it.isCancelled = true
    }

    private val handAnimationHandler: (event: PlayerHandAnimationEvent) -> Unit = eventCallback@{ event: PlayerHandAnimationEvent ->
        val replay = event.player.instance?.recorder?.replay ?: return@eventCallback

        replay.getEntity(event.player)?.let { replay.addAction(RecPlayerHandAnimation(enumValueOf(event.hand.name), it)) }
    }

    fun registerListeners() {
        val eventHandler = MinecraftServer.getGlobalEventHandler()
        eventHandler.addEventCallback(PlayerDisconnectEvent::class.java, playerDisconnectHandler)
        eventHandler.addEventCallback(PlayerSwapItemEvent::class.java, playerSwapItemEvent)
        eventHandler.addEventCallback(PlayerUseItemEvent::class.java, useItemHandler)
        eventHandler.addEventCallback(InventoryPreClickEvent::class.java, inventoryPreClickHandler)

        MinecraftServer.getGlobalEventHandler().addEventCallback(
            PlayerHandAnimationEvent::class.java,
            handAnimationHandler
        )
        registerPacketListener()
    }

    private fun registerPacketListener() {
        MinecraftServer.getConnectionManager().onPacketSend { players, packetController, packet ->
            if (packet is EntityMetaDataPacket) {
                players.filter { it.instance != null }.groupBy { it.instance?.uniqueId }.forEach {
                    val player = it.value.first()
                    val replay = player.instance?.recorder?.replay ?: return@onPacketSend

                    val metadataArray = packet.getMetadataArray()

                    replay.getEntityById(packet.entityId)?.let {
                        replay.addAction(RecEntityMetadata(metadataArray, it))
                    }
                }
            } else if (packet is EntityEquipmentPacket) {
                val entity = Entity.getEntity(packet.entityId) ?: return@onPacketSend
                val instance = entity.instance ?: return@onPacketSend

                val recorder: ReplayRecorder = instance.recorder ?: return@onPacketSend

                recorder.notifyEntityEquipmentChange(entity)
            }
        }
    }
}
