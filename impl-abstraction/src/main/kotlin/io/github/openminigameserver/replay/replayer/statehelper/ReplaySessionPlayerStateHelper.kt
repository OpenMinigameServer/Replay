package io.github.openminigameserver.replay.replayer.statehelper

import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.TimeUnit
import io.github.openminigameserver.replay.abstraction.ReplayActionItemStack
import io.github.openminigameserver.replay.abstraction.ReplayGameMode
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.replayer.ReplaySession
import io.github.openminigameserver.replay.replayer.statehelper.constants.*
import io.github.openminigameserver.replay.replayer.statehelper.utils.ReplayStatePlayerData
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.seconds

class ReplaySessionPlayerStateHelper(val session: ReplaySession) {
    val replayPlatform = session.replayPlatform

    val viewers: List<ReplayUser>
        get() = session.viewers

    val host: ReplayUser?
        get() = viewers.firstOrNull()

    private var isInitialized = true

    private var tickerTask: Any? = null
    private fun getActionBarMessage() = if (!isInitialized) empty() else text {
        val spacing = " ".repeat(6)
        val (minutes, seconds) = formatTime(session.time)
        val (minutesFinal, secondsFinal) = formatTime(session.replay.duration)

        with(it) {


            append((if (session.paused) text("Paused", NamedTextColor.RED) else text("Playing", NamedTextColor.GREEN)))
            append(text(spacing))

            append(text("$minutes:$seconds", NamedTextColor.YELLOW))
            append(text(" / "))
            append(text("$minutesFinal:$secondsFinal", NamedTextColor.YELLOW))

            append(text(spacing))
            append(text("x", NamedTextColor.GOLD))
            append(
                text(
                    (if (session.speed >= 0.5) "%.1f" else "%.2f").format(
                        session.speed,
                        Locale.ENGLISH
                    ), NamedTextColor.GOLD
                )
            )
            append(text(" ".repeat(2)))
        }
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

    private fun updateViewersActionBar(viewers: MutableList<ReplayUser>) {
        viewers.forEach {
            it.sendActionBar(getActionBarMessage())
        }
    }

    fun init() {
        initializePlayerControlItems()
        teleportViewers()
        playLoadedSoundToViewers()
        tickerTask =
            replayPlatform.registerSyncRepeatingTask(TickTime(250, TimeUnit.MILLISECOND)) { tickerTaskRunnable.run() }
    }

    private fun playLoadedSoundToViewers() {
        viewers.forEach {
            try {
                it.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.PLAYER, 1f, 1f))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private val oldData = mutableMapOf<UUID, ReplayStatePlayerData>()
    fun removeViewer(player: ReplayUser) {
        oldData[player.uuid]?.apply(replayPlatform, player)
    }

    private fun initializePlayerControlItems() {
        session.viewers.forEach { p: ReplayUser ->
            oldData[p.uuid] = ReplayStatePlayerData(replayPlatform, p)
            p.clearInventory()
            p.gameMode = ReplayGameMode.ADVENTURE
            p.isAllowFlying = true
            p.isFlying = true
        }

        host?.let {
            it.setHeldItemSlot(SLOT_PLAY_PAUSE.toByte())
            updateItems(it)
        }

    }

    private fun updateItems(it: ReplayUser) {
        it.setItemStack(
            SLOT_DECREASE_SPEED,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getDecreaseSpeedItem())
        )
        it.setItemStack(
            SLOT_STEP_BACKWARDS,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getStepBackwardsItem(session.currentStepDuration))
        )

        it.setItemStack(SLOT_PLAY_PAUSE, PlayerHeadsItems.getPlayPauseItem(session.paused, session.hasEnded))

        it.setItemStack(
            SLOT_STEP_FORWARD,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getStepForwardItem(session.currentStepDuration))
        )
        it.setItemStack(
            SLOT_INCREASE_SPEED,
            getItemStackOrAirIfReplayEnded(PlayerHeadsItems.getIncreaseSpeedItem())
        )
    }

    private fun getItemStackOrAirIfReplayEnded(itemStack: ReplayActionItemStack) =
        if (session.hasEnded) ReplayActionItemStack.air else itemStack

    val skipSpeeds = arrayOf(1, 5, 10, 30, 60)
    private val speeds = arrayOf(0.25, 0.5, 1.0, 2.0, 4.0)
    fun handleItemAction(player: ReplayUser, action: ControlItemAction) {
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
        try {
            player.playSound(Sound.sound(Key.key("block.lever.click"), Sound.Source.BLOCK, 1f, 1f))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
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
            entities.firstOrNull { (it.entityData as? PlayerEntityData)?.userName == session.viewers.first().name }
                ?: entities.firstOrNull()
        val targetEntityMinestom = targetEntity?.let { session.entityManager.getNativeEntity(it) }

        if (targetEntityMinestom != null) {
            session.viewers.forEach { it.teleport(targetEntityMinestom.position) }
        }
    }

    fun unInit() {
        tickerTask?.let { replayPlatform.cancelTask(it) }
        tickerTask = null
        isInitialized = false
        updateViewersActionBar(session.viewers)
    }

    fun updateReplayStateToViewers() {
        updateViewersActionBar(session.viewers)
        updateAllItems()
    }

    fun handleItemSwing(player: ReplayUser, itemStack: ReplayActionItemStack) {
        val action = itemStack.action.takeUnless { it == ControlItemAction.NONE }
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