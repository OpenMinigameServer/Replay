package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.model.Replay
import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.time.TimeUnit

object ReplaySessionManager {

    @JvmStatic
    fun createReplaySession(
        replay: Replay,
        viewers: MutableList<Player>,
        instance: Instance? = null,
        tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
    ): ReplaySession {
        val hasChunks = replay.hasChunks
        val finalInstance =
            if (hasChunks) createEmptyReplayInstance(replay) else instance!!

        return ReplaySession(finalInstance, replay, viewers, tickTime)
    }

    @JvmStatic
    private fun createEmptyReplayInstance(replay: Replay) =
        MinecraftServer.getInstanceManager().createInstanceContainer().apply {
            enableAutoChunkLoad(true)
            data = DataImpl()
            chunkLoader = ReplayChunkLoader(replay)
        }
}