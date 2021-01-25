package io.github.openminigameserver.replay.player.statehelper

import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.player.ReplaySession
import io.github.openminigameserver.replay.player.statehelper.constants.*
import io.github.openminigameserver.replay.player.statehelper.utils.ReplayStatePlayerData
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.sound.Sound
import net.minestom.server.sound.SoundCategory
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.seconds

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
        updateViewersActionBar(session.viewers.toMutableList())
    }

    private fun updateAllItems() {
        host?.let { updateItems(it) }
    }

    private fun updateViewersActionBar(viewers: MutableList<Player>) {
        viewers.forEach {
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
                .repeat(250, TimeUnit.MILLISECOND)
                .schedule()
    }

    private fun playLoadedSoundToViewers() {
        viewers.forEach { it.playSound(Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f) }
    }

    private val oldData = mutableMapOf<UUID, ReplayStatePlayerData>()
    fun removeViewer(player: Player) {
        oldData[player.uuid]?.apply(player)
    }

    private fun initializePlayerControlItems() {
        session.viewers.forEach { p: Player ->
            oldData[p.uuid] = ReplayStatePlayerData(p)
            p.inventory.clear()
            p.gameMode = GameMode.ADVENTURE
            p.isAllowFlying = true
            p.isFlying = true
        }

        host?.let {
            it.setHeldItemSlot(SLOT_PLAY_PAUSE.toByte())
            updateItems(it)
        }

    }

    private fun updateItems(it: Player) {
        it.inventory.setItemStack(
            SLOT_DECREASE_SPEED,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getDecreaseSpeedItem())
        )
        it.inventory.setItemStack(
            SLOT_STEP_BACKWARDS,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getStepBackwardsItem(session.currentStepDuration))
        )

        it.inventory.setItemStack(SLOT_PLAY_PAUSE, PlayerHeadsItems.getPlayPauseItem(session.paused, session.hasEnded))

        it.inventory.setItemStack(
            SLOT_STEP_FORWARD,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getStepForwardItem(session.currentStepDuration))
        )
        it.inventory.setItemStack(
            SLOT_INCREASE_SPEED,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getIncreaseSpeedItem())
        )
    }

    private fun getItemStackOrAirIfReplayEnded(itemStack: ItemStack) =
        if (session.hasEnded) ItemStack.getAirItem() else itemStack

    val skipSpeeds = arrayOf(1, 5, 10, 30, 60)
    private val speeds = arrayOf(0.25, 0.5, 1.0, 2.0, 4.0)
    fun handleItemAction(player: Player, action: ControlItemAction) {

        when (action) {
            ControlItemAction.COOL_DOWN -> {
                val previousSpeed = speeds[(speeds.indexOf(session.speed) - 1).coerceAtLeast(0)]
                session.speed = previousSpeed
                session.tick(true)
            }
            ControlItemAction.PAUSE -> {
                session.paused = true
            }
            ControlItemAction.RESUME -> {
                session.paused = false
            }
            ControlItemAction.PLAY_AGAIN -> {
                session.lastReplayTime = session.replay.duration
                session.time = Duration.ZERO

                session.tick(forceTick = action == ControlItemAction.PLAY_AGAIN, isTimeStep = true)
                session.paused = false
            }
            ControlItemAction.SPEED_UP -> {
                val nextSpeed = speeds[(speeds.indexOf(session.speed) + 1).coerceAtMost(speeds.size - 1)]
                session.speed = nextSpeed
                session.tick(true)
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
        val oldPausedState = session.paused
        val duration = session.currentStepDuration * if (isForward) 1 else -1
        session.paused = true

        session.lastReplayTime = session.time
        session.time =
            (session.time + duration).coerceIn(Duration.ZERO, session.replay.duration)
        session.tick(forceTick = true, isTimeStep = true)

        session.paused = oldPausedState
        updateReplayStateToViewers()
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
        updateViewersActionBar(session.viewers)
    }

    fun updateReplayStateToViewers() {
        updateViewersActionBar(session.viewers)
        updateAllItems()
    }

    fun handleItemSwing(player: Player, itemStack: ItemStack) {
        val action = itemStack.controlItemAction.takeUnless { it == ControlItemAction.NONE }
        if (action == ControlItemAction.STEP_BACKWARDS || action == ControlItemAction.STEP_FORWARD) {
            val duration = session.currentStepDuration.inSeconds.roundToInt()
            val currentSkipIndex = skipSpeeds.indexOf(duration).coerceAtLeast(0)
            var nextIndex = currentSkipIndex + 1
            if (nextIndex >= skipSpeeds.size) {
                nextIndex = 0
            }

            session.currentStepDuration = skipSpeeds[nextIndex].seconds

        }
    }

}