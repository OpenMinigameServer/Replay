package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.player.helpers.EntityManager
import io.github.openminigameserver.replay.player.inventory.ReplaySessionPlayerStateHelper
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration

class ReplaySession(internal val instance: Instance, val replay: Replay, val viewers: MutableList<Player>) {
    private var tickerTask: Task? = null

    private val playerStateHelper = ReplaySessionPlayerStateHelper(this)
    private val ticker: Runnable = ReplayTicker(this)
    private val actions = Stack<RecordableAction>()

    init {
        resetActions()
    }

    private fun resetActions(targetDuration: Duration = Duration.ZERO) {
        actions.addAll(replay.actions.filter { it.timestamp >= targetDuration }.sortedByDescending { it.timestamp })
    }

    var speed: Double = 1.0
        set(value) {
            field = value
            updateReplayStateToViewers()
        }

    var paused = true

    var hasSpawnedEntities = false

    /**
     * Last timestamp in milliseconds the [tick] method was called.
     */
    private var lastTickTime: Instant = Clock.System.now()

    /**
     * Current replay time.
     * Valid regardless of [paused].
     */
    var time: Duration = Duration.ZERO
        set(value) {
            field = value
            updateReplayStateToViewers()
        }

    private fun updateReplayStateToViewers() {
        playerStateHelper.updateViewerActionBar()
    }


    private val viewerTeam: Team = MinecraftServer.getTeamManager().createBuilder("ReplayViewers")
        .prefix(ColoredText.of(ChatColor.GRAY, "[Viewer] "))
        .collisionRule(TeamsPacket.CollisionRule.NEVER)
        .teamColor(ChatColor.GRAY)
        .build()

    fun init() {
        viewers.forEach { p ->
            viewerTeam.addMember(p.username)
        }
        tickerTask = MinecraftServer.getSchedulerManager().buildTask(ticker).repeat(1, TimeUnit.TICK).schedule()
        playerStateHelper.init()
    }

    private fun unInit() {
        tickerTask?.cancel()
        entityManager.removeAllEntities()
        instance.replaySession = null
        playerStateHelper.unInit()
        viewers.forEach { removeViewer(it) }
    }

    fun removeViewer(player: Player) {
        entityManager.removeEntityViewer(player)
        viewers.remove(player)

        if (viewerTeam.members.contains(player.username))
            viewerTeam.removeMember(player.username)

        if (viewers.isEmpty()) {
            unInit()
        }
    }

    /**
     * The next action to be played.
     */
    private var nextAction: RecordableAction? = null

    /**
     * Update the current time and play actions accordingly.
     */
    private var lastReplayTime = Duration.ZERO /* Used to detect if we're going backwards */

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
        val targetReplayTime = (this.time + (timePassed * speed))

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
                    time = replay.duration

                    return
                }
            } else {
                if (nextAction!!.timestamp < targetReplayTime) {
                    playAction(nextAction!!)
                    this.time = nextAction!!.timestamp
                    nextAction = null
                } else {
                    this.time += timePassed * speed
                    break
                }
            }
        }
        lastTickTime = currentTime
        lastReplayTime = this.time
    }

    val entityManager = EntityManager(this)
    private fun playAction(action: RecordableAction) {
        try {
            ActionPlayerManager.getActionPlayer(action).play(action, this, instance, viewers)
        } catch (e: Throwable) {
            e.printStackTrace()

            paused = true
            viewers.forEach {
                it.sendMessage(
                    ColoredText.of(
                        ChatColor.RED,
                        "An error occurred while playing your replay. Please contact an administrator for support."
                    )
                )
            }

            //Unload everything
            unInit()
        }
    }
}