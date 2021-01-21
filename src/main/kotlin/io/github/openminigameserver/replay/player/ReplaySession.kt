@file:OptIn(ExperimentalTime::class)

package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.model.ReplayFile
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.player.helpers.EntityManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ReplaySession(internal val instance: Instance, val replay: ReplayFile, val viewers: MutableList<Player>) {
    private var tickerTask: Task? = null

    private val ticker: Runnable = ReplayTicker(this)
    private val actions = Stack<RecordableAction>()

    init {
        resetActions()
    }

    private fun resetActions(targetDuration: Duration = Duration.ZERO) {
        actions.addAll(replay.actions.filter { it.timestamp>= targetDuration }.sortedByDescending { it.timestamp })
    }

    var speed: Double = 1.0
    var paused = true

    var hasSpawnedEntities = false

    /**
     * Last timestamp in milliseconds the [process] method was called.
     */
    private var lastTickTime: Instant = Clock.System.now()

    /**
     * Current replay time.
     * Valid regardless of [paused].
     */
    private var currentReplayTime: Duration = Duration.ZERO

    var time: Duration
    get() = currentReplayTime
    set(value) {
        currentReplayTime = value
    }

    fun init() {
        tickerTask = MinecraftServer.getSchedulerManager().buildTask(ticker).repeat(1, TimeUnit.TICK).schedule()
    }

    fun removeViewer(player: Player) {
        entityManager.removeEntityViewer(player)
        viewers.remove(player)

        if (viewers.isEmpty()) {
            unInit()
        }
    }

    private fun unInit() {
        tickerTask?.cancel()
        entityManager.removeAllEntities()
        instance.replaySession = null
    }

    /**
     * The next action to be played.
     */
    private var nextAction: RecordableAction? = null

    /**
     * Update the current time and play actions accordingly.
     */
    private var lastReplayTime = Duration.ZERO /* Used to detect if we're going backwards */

    @OptIn(ExperimentalTime::class)
    internal fun tick() {
        if (!hasSpawnedEntities) {
            replay.entities.values.filter { it.spawnOnStart }.forEach {
                entityManager.spawnEntity(it, it.spawnPosition!!)
            }
            hasSpawnedEntities = true
        }

        val currentTime = Clock.System.now()
        if (paused) {
            lastTickTime = currentTime
            return
        }

        val timePassed = currentTime - lastTickTime
        val targetReplayTime = (currentReplayTime + (timePassed * speed))

        if (targetReplayTime < lastReplayTime) {
            // Need to restart replay to go backwards in time
            replay.entities.values.forEach { entityManager.resetEntity(it) }
            resetActions(targetReplayTime)
        }

        fun readNextAction() {
            nextAction = actions.takeIf { !it.empty() }?.pop()
        }

        while (true) {
            if (nextAction == null) {
                readNextAction()
                if (nextAction == null) {
                    // If still null, then we reached end of replay
                    paused = true
                    resetActions()
                    currentReplayTime = Duration.ZERO

                    return
                }
            } else {
                if (nextAction!!.timestamp < targetReplayTime) {
                    playAction(nextAction!!)
                    currentReplayTime = nextAction!!.timestamp
                    nextAction = null
                } else {
                    currentReplayTime += timePassed * speed
                    break
                }
            }
        }
        lastTickTime = currentTime
        lastReplayTime = currentReplayTime
    }

    val entityManager = EntityManager(this)
    internal fun playAction(action: RecordableAction) {
        ActionPlayerManager.getActionPlayer(action).play(action, this, instance, viewers)
    }
}