package io.github.openminigameserver.replay.test

import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.InstanceManager
import net.minestom.server.utils.Position


class PlayerInit : EventCallback<PlayerLoginEvent> {

    private val instanceManager: InstanceManager = MinecraftServer.getInstanceManager()

    // Create the instance
    private val instanceContainer by lazy {
        instanceManager.createInstanceContainer().apply {
            chunkGenerator = MyChunkGenerator()
            data = DataImpl()
            enableAutoChunkLoad(true)
        }
    }

    override fun run(event: PlayerLoginEvent) {
        val player = event.player
        player.data = DataImpl()
        player.gameMode = GameMode.CREATIVE
        player.isAllowFlying = true
        player.isFlying = true

        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Position(0.0, 42.0, 0.0)
    }
}


