package io.github.openminigameserver.replay.platform.bukkit

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import io.github.openminigameserver.replay.BukkitActionPlayerManager
import io.github.openminigameserver.replay.ReplayPlugin
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.platform.IdHelperContainer
import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.replayer.ActionPlayerManager
import io.github.openminigameserver.replay.replayer.IEntityManager
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Team
import java.io.File
import java.util.*

class BukkitReplayPlatform : ReplayPlatform<BukkitReplayWorld, BukkitReplayUser, BukkitReplayEntity>() {
    override val worlds: IdHelperContainer<UUID, BukkitReplayWorld> =
        IdHelperContainer { BukkitReplayWorld(Bukkit.getWorld(this)!!) }
    override val entities: IdHelperContainer<Int, ReplayEntity> = IdHelperContainer {
        val entity = Bukkit.getWorlds().flatMap { it.entities }.find { it.entityId == this }!!
        if (entity is Player)
            BukkitReplayUser(entity)
        else
            BukkitReplayEntity(entity)
    }
    override val name: String
        get() = Bukkit.getName()
    override val version: String
        get() = Bukkit.getBukkitVersion()

    override val dataDir: File
        get() = ReplayPlugin.instance.dataFolder

    override fun log(message: String) {
        ReplayPlugin.instance.logger.info(message)
    }

    override val commandManager = PaperCommandManager<ReplayUser>(
        ReplayPlugin.instance,
        AsynchronousCommandExecutionCoordinator.newBuilder<ReplayUser>().withAsynchronousParsing().build(),
        {
            entities.getOrCompute((it as Player).entityId) as ReplayUser
        },
        {
            (it as BukkitReplayUser).player
        }
    )
    override val commandAnnotationParser: AnnotationParser<ReplayUser> =
        AnnotationParser(commandManager, ReplayUser::class.java) { commandManager.createDefaultCommandMeta() }

    override fun registerSyncRepeatingTask(time: TickTime, action: () -> Unit): Any {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(
            ReplayPlugin.instance,
            { action() },
            0L,
            time.unit.toMilliseconds(time.time) / 50
        )
    }

    override fun cancelTask(tickerTask: Any) {
        Bukkit.getScheduler().cancelTask(tickerTask as Int)
    }

    override fun getEntityType(replayEntity: BukkitReplayEntity): String {
        return replayEntity.entity.type.key.toString()
    }

    override fun getWorldById(it: UUID): BukkitReplayWorld {
        return worlds.getOrCompute(it)
    }

    override fun unregisterWorld(instance: BukkitReplayWorld) {
        Bukkit.unloadWorld(instance.world, false)
    }

    private val replayViewerTeamName = "replay-viewer"
    override fun addToViewerTeam(p: BukkitReplayUser) {
        Bukkit.getScheduler().runTask(ReplayPlugin.instance, Runnable {
            with(p.player) {
                val team = getViewerTeam()
                team.prefix(Component.text("[Viewer] ", NamedTextColor.GRAY))
                team.color(NamedTextColor.GRAY)

                team.addEntry(name)
            }
        })
    }

    private fun Player.getViewerTeam(): Team {
        if (scoreboard == Bukkit.getScoreboardManager().mainScoreboard) {
            scoreboard = Bukkit.getScoreboardManager().newScoreboard
        }

        return scoreboard.getTeam(replayViewerTeamName) ?: scoreboard.registerNewTeam(replayViewerTeamName)
    }

    override fun removeFromViewerTeam(player: BukkitReplayUser) {
        Bukkit.getScheduler().runTask(ReplayPlugin.instance, Runnable {
            with(player.player) {
                val team = getViewerTeam()
                team.removeEntry(name)
            }
        })
    }

    override fun getPlayerInventoryCopy(player: BukkitReplayUser): Any {
        return player.player.inventory.contents
    }

    override fun loadPlayerInventoryCopy(player: BukkitReplayUser, inventory: Any) {
        player.player.inventory.setContents(inventory as Array<out ItemStack>)
    }

    override fun createReplaySession(
        replay: Replay,
        viewers: MutableList<ReplayUser>,
        instance: ReplayWorld?,
        tickTime: TickTime
    ): ReplaySession {
        return ReplaySession(
            this as ReplayPlatform<ReplayWorld, ReplayUser, ReplayEntity>,
            instance!!,
            replay,
            viewers,
            tickTime
        )
    }

    override fun getEntityManager(replaySession: ReplaySession): IEntityManager<BukkitReplayUser, BukkitReplayEntity> {
        return BukkitEntityManager(this, replaySession)
    }

    override val actionPlayerManager: ActionPlayerManager<BukkitReplayWorld, BukkitReplayUser, BukkitReplayEntity> =
        BukkitActionPlayerManager(this)

    override fun getEntityData(replayEntity: BukkitReplayEntity): BaseEntityData? {
        return null
    }

}