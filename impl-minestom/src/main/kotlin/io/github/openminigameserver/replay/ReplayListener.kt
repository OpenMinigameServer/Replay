package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.extensions.*
import io.github.openminigameserver.replay.helpers.ReplayPlayerEntity
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.impl.*
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import io.github.openminigameserver.replay.replayer.statehelper.constants.controlItemAction
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.event.instance.AddEntityToInstanceEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.packet.server.play.*

object ReplayListener {

    private val playerDisconnectHandler: (event: PlayerDisconnectEvent) -> Unit = {
        if (it.player !is FakePlayer && it.player !is ReplayPlayerEntity)
            it.player.instance!!.replaySession?.removeViewer(it.player)
    }
    private val viewerJoinedReplaySession: (event: AddEntityToInstanceEvent) -> Unit = event@{
        val player = it.entity as? Player ?: return@event
        val instance = it.instance
        val session = instance.replaySession ?: return@event

        val isViewer = session.viewers.any { it.uuid == player.uuid }
        if (!isViewer) return@event

        session.viewerCountDownLatch.countDown()
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

    private val handAnimationHandler: (event: PlayerHandAnimationEvent) -> Unit =
        eventCallback@{ event: PlayerHandAnimationEvent ->
            val session = event.player.instance?.replaySession ?: return@eventCallback

            session.playerStateHelper.handleItemSwing(event.player, event.player.getItemInHand(event.hand))
        }

    fun registerListeners() {
        val eventHandler = MinecraftServer.getGlobalEventHandler()
        eventHandler.addEventCallback(PlayerDisconnectEvent::class.java, playerDisconnectHandler)
        eventHandler.addEventCallback(AddEntityToInstanceEvent::class.java, viewerJoinedReplaySession)
        eventHandler.addEventCallback(PlayerSwapItemEvent::class.java, playerSwapItemEvent)
        eventHandler.addEventCallback(PlayerUseItemEvent::class.java, useItemHandler)
        eventHandler.addEventCallback(InventoryPreClickEvent::class.java, inventoryPreClickHandler)

        MinecraftServer.getGlobalEventHandler().addEventCallback(
            PlayerHandAnimationEvent::class.java,
            handAnimationHandler
        )
    }

    @JvmStatic
    fun handleSentPacket(
        packet: ServerPacket,
        players: Collection<Player>
    ) {
        when (packet) {
            is SoundEffectPacket -> {
                handleRecording(players) { replay ->
                    replay.addAction(packet.run {
                        RecSoundEffect(soundId, SoundCategory.valueOf(soundCategory.name), x, y, z, volume, pitch)
                    })
                }
            }
            is EntityMetaDataPacket -> {
                val entity = Entity.getEntity(packet.entityId) as? Player
                handleRecording(entity?.let { players.plus(entity) } ?: players) { replay ->
                    val metadataArray = packet.getMetadataArray()

                    replay.getEntityById(packet.entityId)?.let {
                        replay.addAction(RecEntityMetadata(metadataArray, it))
                    }
                }
            }
            is EntityEquipmentPacket -> {
                val entity = Entity.getEntity(packet.entityId) ?: return
                val instance = entity.instance ?: return

                val recorder: ReplayRecorder = instance.recorder ?: return

//TODO:                recorder.notifyEntityEquipmentChange(entity)
            }
            is EffectPacket -> {
                handleEffectPacket(players, packet)
            }
            is ParticlePacket -> {
                handleRecording(players) { replay ->
                    replay.addAction(
                        RecParticleEffect(
                            packet.particleId,
                            packet.longDistance,
                            packet.x,
                            packet.y,
                            packet.z,
                            packet.offsetX,
                            packet.offsetY,
                            packet.offsetZ,
                            packet.particleData,
                            packet.particleCount,
                            packet.dataConsumer?.getMetadataArray()
                        )
                    )
                }
            }
        }
    }

    private fun handleEffectPacket(
        players: Collection<Player>,
        packet: EffectPacket
    ) {
        handleRecording(players) { replay ->
            replay.addAction(
                RecBlockEffect(
                    packet.effectId,
                    packet.position.toPosition().toReplay(),
                    packet.data,
                    packet.disableRelativeVolume
                )
            )
        }
    }

    private inline fun handleRecording(players: Collection<Player>, code: (Replay) -> Unit) {
        players.filter { it.instance != null }.groupBy { it.instance?.uniqueId }.forEach {
            val player = it.value.first()
            val replay = player.instance?.recorder?.replay ?: return@forEach

            code.invoke(replay)
        }
    }
}
