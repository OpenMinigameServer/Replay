package io.github.openminigameserver.replay.player.inventory

import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.player.ReplaySession
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.entity.Player
import net.minestom.server.sound.Sound
import net.minestom.server.sound.SoundCategory
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration

class ReplaySessionPlayerStateHelper(val session: ReplaySession) {

    val viewers: List<Player>
        get() = session.viewers

    val host: Player?
        get() = viewers.firstOrNull()

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
        append(
            "${ChatColor.GOLD}x${
                (if (session.speed >= 0.5) "%.1f" else "%.2f").format(
                    session.speed,
                    Locale.ENGLISH
                )
            }"
        )
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
        updateViewersActionBar()
    }

    private fun updateAllItems() {
        host?.let { updateItems(it) }
    }

    internal fun updateViewersActionBar() {
        session.viewers.forEach {
            it.sendActionBarMessage(ColoredText.of(getActionBarMessage()))
        }
    }

    fun init() {
        initializePlayerControlItems()
        teleportViewers()
        playLoadedSoundToViewers()
        tickerTask =
            MinecraftServer.getSchedulerManager()
                .buildTask(tickerTaskRunnable)
                .repeat(1, TimeUnit.SECOND)
                .schedule()
    }

    private fun playLoadedSoundToViewers() {
        viewers.forEach { it.playSound(Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f) }
    }

    private fun initializePlayerControlItems() {
        session.viewers.forEach {
            it.inventory.clear()
        }
        host?.let {
            updateItems(it)
        }

    }

    private fun updateItems(it: Player) {
        //0 1 2 3 4 5 6 7 8
        //    2 3 4 5 6
        it.inventory.setItemStack(2, PlayerHeadsItems.getDecreaseSpeedItem())
        it.inventory.setItemStack(3, PlayerHeadsItems.getStepBackwardsItem(session.currentSkipDuration))

        it.inventory.setItemStack(4, PlayerHeadsItems.getPlayPauseItem(session.paused))

        it.inventory.setItemStack(5, PlayerHeadsItems.getStepForwardItem(session.currentSkipDuration))
        it.inventory.setItemStack(6, PlayerHeadsItems.getIncreaseSpeedItem())
    }

    val skipSpeeds = arrayOf(1, 5, 10, 30, 60)
    private val speeds = arrayOf(0.25, 0.5, 1.0, 2.0, 4.0)
    fun handleItemAction(player: Player, action: ControlItemAction) {

        when (action) {
            ControlItemAction.COOL_DOWN -> {
                val previousSpeed = speeds[(speeds.indexOf(session.speed) - 1).coerceAtLeast(0)]
                session.speed = previousSpeed
                sendSubtitleToHost("-")
                session.tick(true)
            }
            ControlItemAction.PAUSE -> {
                session.paused = true
                sendSubtitleToHost("⏸")
            }
            ControlItemAction.RESUME -> {
                val hasEnded = session.hasEnded
                if (hasEnded) {
                    session.time = Duration.ZERO
                }
                session.paused = false
                if (hasEnded)
                    session.tick(isTimeStep = true)

                sendSubtitleToHost("⏵")
            }
            ControlItemAction.SPEED_UP -> {
                val nextSpeed = speeds[(speeds.indexOf(session.speed) + 1).coerceAtMost(speeds.size - 1)]
                session.speed = nextSpeed
                session.tick(true)
                sendSubtitleToHost("+")
            }
            ControlItemAction.STEP_BACKWARDS -> {
                doStep(false)
            }
            ControlItemAction.STEP_FORWARD -> {
                doStep(true)

            }
            else -> TODO(action.name)
        }

        player.playSound(Sound.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f)
        updateReplayStateToViewers()
    }

    private fun doStep(isForward: Boolean) {
        val duration = session.currentSkipDuration * if (isForward) 1 else -1
        session.time =
            (session.time + duration).coerceIn(Duration.ZERO, session.replay.duration)
        sendSubtitleToHost(if (isForward) "⏩" else "⏪")
        session.tick(forceTick = true, isTimeStep = true)
    }

    private fun sendSubtitleToHost(message: String) {
        host?.sendTitleSubtitleMessage(ColoredText.of(""), ColoredText.of(ChatColor.RED, message))
    }

    private fun teleportViewers() {
        val entities = session.replay.entities.values
        val targetEntity =
            entities.firstOrNull { (it.entityData as? PlayerEntityData)?.userName == session.viewers.first().username }
                ?: entities.firstOrNull()
        val targetEntityMinestom = targetEntity?.let { session.entityManager.getNativeEntity(it) }

        if (targetEntityMinestom != null) {
            session.viewers.forEach { it.teleport(targetEntityMinestom.position) }
        }
    }

    fun unInit() {
        tickerTask?.cancel()
        tickerTask = null
        isInitialized = false
        updateViewersActionBar()
    }

    fun updateReplayStateToViewers() {
        updateViewersActionBar()
        updateAllItems()
    }

}