package io.github.openminigameserver.replay.player.inventory

import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration

class ReplaySessionPlayerStateHelper(val session: ReplaySession) {

    private var isInitialized = true

    private var tickerTask: Task? = null
    private fun getActionBarMessage() = if (!isInitialized) "" else buildString {
        val spacing = " ".repeat(6)
        val (minutes, seconds) = formatTime(session.time)
        val (minutesFinal, secondsFinal) = formatTime(session.replay.duration)

        append(if (session.paused) ChatColor.RED.toString() + "Paused" else ChatColor.BRIGHT_GREEN.toString() + "Playing")
        append(spacing)

        append("${ChatColor.YELLOW}$minutes:$seconds")
        append(" / ")
        append("${ChatColor.YELLOW}$minutesFinal:$secondsFinal")

        append(spacing)
        append("${ChatColor.GOLD}x${"%.1f".format(session.speed, Locale.ENGLISH)}")
        append(" ".repeat(2))
    }

    private fun formatTime(time: Duration): Pair<String, String> {
        val currentTime = time.inSeconds
        val minutes = formatResultToTime(currentTime / 60)
        val seconds = formatResultToTime(currentTime % 60)
        return Pair(minutes, seconds)
    }

    private fun formatResultToTime(currentTime: Double) = (currentTime).toInt().toString().padStart(2, '0')

    private val tickerTaskRunnable = Runnable {
        updateViewerActionBar()
    }

    internal fun updateViewerActionBar() {
        session.viewers.forEach {
            it.sendActionBarMessage(ColoredText.of(getActionBarMessage()))
        }
    }

    fun init() {
        teleportViewers()
        tickerTask =
            MinecraftServer.getSchedulerManager()
                .buildTask(tickerTaskRunnable)
                .repeat(2, TimeUnit.SECOND)
                .schedule()
    }

    private fun teleportViewers() {
        val entities = session.replay.entities.values
        val targetEntity = entities.firstOrNull { (it.entityData as? PlayerEntityData)?.userName == session.viewers.first().username } ?: entities.firstOrNull()
        val targetEntityMinestom = targetEntity?.let { session.entityManager.getNativeEntity(it) }

        if (targetEntityMinestom != null) {
            session.viewers.forEach { it.teleport(targetEntityMinestom.position) }
        }
    }

    fun unInit() {
        tickerTask?.cancel()
        tickerTask = null
        isInitialized = false
        updateViewerActionBar()
    }

}