package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.helpers.EntityManager
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.player.statehelper.ReplaySessionPlayerStateHelper
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.time.Duration
import kotlin.time.seconds

class ReplaySession constructor(
    internal val instance: Instance,
    val replay: Replay,
    val viewers: MutableList<Player>,
    private val tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
) {
    var currentSkipDuration = 10.seconds
    var isInitialized = false


    val hasEnded: Boolean
        get() = time == replay.duration

    val playerStateHelper = ReplaySessionPlayerStateHelper(this)
    private val ticker: Runnable = ReplayTicker(this)
    private val actions = Stack<RecordableAction>()

    init {
        resetActions()
    }

    inline fun <reified T : RecordableAction> findPrevious(
        targetDuration: Duration = Duration.ZERO,
        condition: (T) -> Boolean = { true }
    ): T? {
        return replay.actions.filter { it.timestamp <= targetDuration }.lastOrNull { it is T && condition(it) } as? T
    }

    inline fun <reified T : EntityRecordableAction> findPreviousForEntity(
        entity: RecordableEntity,
        targetDuration: Duration = Duration.ZERO,
        condition: (T) -> Boolean = { true }
    ): T? {
        return findPrevious(targetDuration) { it.entity == entity && condition(it) }
    }

    private fun resetActions(targetDuration: Duration = Duration.ZERO) {
        actions.clear()
        actions.addAll(replay.actions.filter { it.timestamp >= targetDuration }.sortedByDescending { it.timestamp })
    }

    var speed: Double = 1.0
        set(value) {
            field = value
            updateReplayStateToViewers()
        }

    var paused = true
        set(value) {
            field = value
            updateReplayStateToViewers()
        }

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

    private fun updateReplayStateToViewers() {
        playerStateHelper.updateReplayStateToViewers()
    }

    private val viewerTeam: Team = MinecraftServer.getTeamManager().createBuilder("ReplayViewers")
        .prefix(ColoredText.of(ChatColor.GRAY, "[Viewer] "))
        .collisionRule(TeamsPacket.CollisionRule.NEVER)
        .teamColor(ChatColor.GRAY)
        .build()

    fun init() {
        isInitialized = true
        viewers.forEach { p ->
            viewerTeam.addMember(p.username)
        }

        Thread {
            while (isInitialized) {
                ticker.run()
                Thread.sleep(tickTime.unit.toMilliseconds(tickTime.time))
            }
        }.start()
    }

    private fun unInit() {
        isInitialized = false;
        entityManager.removeAllEntities()
        instance.replaySession = null
        playerStateHelper.unInit()
        viewers.forEach { removeViewer(it) }
    }

    fun removeViewer(player: Player) {
        entityManager.removeEntityViewer(player)
        playerStateHelper.removeViewer(player)
        viewers.remove(player)

        if (viewerTeam.members.contains(player.username))
            viewerTeam.removeMember(player.username)

        player.sendActionBarMessage(ColoredText.of(""))
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

    internal fun tick(forceTick: Boolean = false, isTimeStep: Boolean = false) {
        if (!hasSpawnedEntities) {
            replay.entities.values.filter { it.spawnOnStart }.forEach {
                entityManager.spawnEntity(it, it.spawnPosition!!)
                playerStateHelper.init()
            }
            hasSpawnedEntities = true
        }

        val currentTime = Clock.System.now()
        if (!forceTick && paused) {
            lastTickTime = currentTime
            return
        }

        val timePassed = currentTime - lastTickTime
        val targetReplayTime = (this.time + (timePassed * speed))

        if (isTimeStep) {
            replay.entities.values.forEach { entityManager.resetEntity(it, targetReplayTime) }
            resetActions(targetReplayTime)
            nextAction = null
            lastTickTime = currentTime
            return
        }

        fun readNextAction() {
            nextAction = actions.takeIf { !it.empty() }?.pop()
        }

        while (true) {
            if (nextAction == null) {
                readNextAction()
                if (nextAction == null) {
                    // If still null, then we reached end of replay
                    time = replay.duration
                    paused = true

                    return
                }
            } else {
                if (nextAction!!.timestamp < targetReplayTime) {
                    playAction(nextAction!!)
                    this.time = nextAction?.timestamp ?: Duration.ZERO
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