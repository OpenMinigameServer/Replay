package io.github.openminigameserver.replay.player.inventory

import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*

class ReplaySessionPlayerStateHelper(val session: ReplaySession) {

    private var tickerTask: Task? = null
    private val actionBarMessage
        get() = buildString {
            val currentTime = session.time.inSeconds
            val spacing = " ".repeat(6)
            val minutes = formatResultToTime(currentTime / 60)
            val seconds = formatResultToTime(currentTime % 60)

            append(if (session.paused) ChatColor.RED.toString() + "Paused" else ChatColor.BRIGHT_GREEN.toString() + "Playing")
            append(spacing)

            append("${ChatColor.YELLOW}$minutes:$seconds")

            append(spacing)
            append("${ChatColor.GOLD}${"%.1f".format(session.speed, Locale.ENGLISH)}")
            append(" ".repeat(2))
        }

    private fun formatResultToTime(currentTime: Double) = (currentTime).toInt().toString().padStart(2, '0')

    private val tickerTaskRunnable = Runnable {
        updatePlayerActionBar()
    }

    internal fun updatePlayerActionBar() {
        session.viewers.forEach {
            it.sendActionBarMessage(ColoredText.of(actionBarMessage))
        }
    }

    fun init() {
        tickerTask =
            MinecraftServer.getSchedulerManager()
                .buildTask(tickerTaskRunnable)
                .repeat(2, TimeUnit.SECOND)
                .schedule()
    }

    fun unInit() {
        tickerTask?.cancel()
        tickerTask = null
    }

}