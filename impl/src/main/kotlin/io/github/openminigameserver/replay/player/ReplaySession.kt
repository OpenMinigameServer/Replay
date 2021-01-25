package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.extensions.replaySession
import io.github.openminigameserver.replay.helpers.EntityManager
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.RecordableAction
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
import kotlin.time.Duration
import kotlin.time.seconds

class ReplaySession constructor(
    internal val instance: Instance,
    override val replay: Replay,
    val viewers: MutableList<Player>,
    private val tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
) : AbstractReplaySession() {
    var currentSkipDuration = 10.seconds

    override val hasEnded: Boolean
        get() = time == replay.duration

    val playerStateHelper = ReplaySessionPlayerStateHelper(this)
    private val playerTimeStepHelper = ReplaySessionTimeStepHelper(this)
    private val ticker: Runnable = ReplayTicker(this)

    init {
        resetActions()
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
    override var time: Duration = Duration.ZERO

    private fun updateReplayStateToViewers() {
        playerStateHelper.updateReplayStateToViewers()
    }

    private val viewerTeam: Team = MinecraftServer.getTeamManager().createBuilder("ReplayViewers")
        .prefix(ColoredText.of(ChatColor.GRAY, "[Viewer] "))
        .collisionRule(TeamsPacket.CollisionRule.NEVER)
        .teamColor(ChatColor.GRAY)
        .build()

    override fun init() {
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

    override fun unInit() {
        isInitialized = false
        entityManager.removeAllEntities()
        instance.replaySession = null
        playerStateHelper.unInit()
        viewers.forEach { removeViewer(it) }
    }

    fun removeViewer(player: Player) {
        try {
            entityManager.removeEntityViewer(player)
            playerStateHelper.removeViewer(player)

            if (viewerTeam.members.contains(player.username)) {
                viewerTeam.removeMember(player.username)
            }

            player.sendActionBarMessage(ColoredText.of(""))
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            viewers.remove(player)
            if (viewers.isEmpty()) {
                unInit()
            }
        }
    }

    /**
     * The next action to be played.
     */
    private var nextAction: RecordableAction? = null

    /**
     * Update the current time and play actions accordingly.
     */
    internal var lastReplayTime = Duration.ZERO /* Used to detect if we're going backwards */

    override fun tick(forceTick: Boolean, isTimeStep: Boolean) {
        if (!hasSpawnedEntities) {
            replay.entities.values.filter { it.spawnOnStart }.forEach {
                entityManager.spawnEntity(it, it.spawnPosition!!)
            }
            playerStateHelper.init()
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
            playerTimeStepHelper.performTimeStep(lastReplayTime, targetReplayTime)
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
    }

    val entityManager = EntityManager(this)
    override fun playAction(action: RecordableAction) {
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