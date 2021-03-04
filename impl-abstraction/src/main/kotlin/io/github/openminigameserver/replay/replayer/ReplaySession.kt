package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.AbstractReplaySession
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.TimeUnit
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.replayer.statehelper.ReplaySessionPlayerStateHelper
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration
import kotlin.time.seconds

class ReplaySession internal constructor(
    internal val replayPlatform: ReplayPlatform<ReplayWorld, ReplayUser, ReplayEntity>,
    internal val world: ReplayWorld,
    override val replay: Replay,
    val viewers: MutableList<ReplayUser>,
    private val tickTime: TickTime = TickTime(1L, TimeUnit.TICK)
) : AbstractReplaySession() {

    val viewerCountDownLatch = CountDownLatch(if (replay.hasChunks) viewers.size else 0)

    var currentStepDuration = 10.seconds
        set(value) {
            field = value
            updateReplayStateToViewers()
        }

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

    override fun init() {
        isInitialized = true
        viewers.forEach { p ->
            setupViewer(p)
        }
        Thread {
            viewerCountDownLatch.await()
            while (isInitialized) {
                ticker.run()
                Thread.sleep(tickTime.unit.toMilliseconds(tickTime.time))
            }
        }.start()
    }

    private val oldViewerInstanceMap = mutableMapOf<UUID, UUID>()
    private fun setupViewer(p: ReplayUser) {
        replayPlatform.addToViewerTeam(p)
        if (p.instance != world) {
            oldViewerInstanceMap[p.uuid] = p.instance.uuid
            p.setWorld(world)
        }
    }

    override fun unInit() {
        isInitialized = false
        entityManager.removeAllEntities()
        world.replaySession = null
        playerStateHelper.unInit()
        viewers.forEach { removeViewer(it) }
        if (replay.hasChunks) {
            replayPlatform.unregisterWorld(world)
        }
    }

    fun removeViewer(player: ReplayUser) {
        try {
            entityManager.removeEntityViewer(player)
            playerStateHelper.removeViewer(player)

            replayPlatform.removeFromViewerTeam(player)

            player.sendActionBar(Component.empty())

            val oldInstance =
                oldViewerInstanceMap[player.uuid]?.let { replayPlatform.getWorldById(it) }
            oldInstance?.let { player.setWorld(oldInstance) }
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
                entityManager.spawnEntity(it, it.spawnPosition!!.position, it.spawnPosition!!.velocity)
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

    val entityManager = replayPlatform.getEntityManager(this)
    override fun playAction(action: RecordableAction) {
        try {
            replayPlatform.actionPlayerManager.getActionPlayer(action).play(action, this, world, viewers)
        } catch (e: Throwable) {
            e.printStackTrace()

            paused = true
            viewers.forEach {
                it.sendMessage(
                    Component.text(
                        "An error occurred while playing your replay. Please contact an administrator for support.",
                        NamedTextColor.RED
                    )
                )
            }

            //Unload everything
            unInit()
        }
    }
}