package io.github.openminigameserver.replay.platform.bukkit

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.AdventureComponentConverter
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import io.github.openminigameserver.replay.*
import io.github.openminigameserver.replay.platform.bukkit.packets.WrapperPlayServerPlayerInfo
import io.github.openminigameserver.replay.platform.bukkit.packets.WrapperPlayServerScoreboardTeam
import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object ReplayListener : Listener {

    val removeNpcTagName = "__REMOVE_NPC__"

    init {
        ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(
            ReplayPlugin.instance, ListenerPriority.LOW,
            PacketType.Play.Server.SCOREBOARD_TEAM, PacketType.Play.Server.PLAYER_INFO
        ) {
            override fun onPacketSending(event: PacketEvent) {
                if (event.packetType == PacketType.Play.Server.SCOREBOARD_TEAM) {
                    val wrapper = WrapperPlayServerScoreboardTeam(event.packet)
                    if (wrapper.players.any { it.endsWith(PlayerNameSplittingHelper.npcNameSuffix) } && wrapper.name.startsWith(
                            "CIT-"
                        )) {
                        event.isCancelled = true
                    }
                } else if (event.packetType == PacketType.Play.Server.PLAYER_INFO) {
                    val wrapper = WrapperPlayServerPlayerInfo(event.packet)

                    val toRemoveDisplay = mutableListOf<PlayerInfoData>()
                    wrapper.data.forEach {
                        if (it.displayName != null) {
                            val text = AdventureComponentConverter.fromWrapper(it.displayName) as TextComponent
                            val isNPC = it.profile.name.endsWith(PlayerNameSplittingHelper.npcNameSuffix)
                            if (!isNPC) return

                            //Cancel initial remove entry
                            if (text.content() != removeNpcTagName && wrapper.action == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER) {
                                event.isCancelled = true
                                return
                            } else if (text.content().isEmpty()) {
                                toRemoveDisplay.add(it)
                            }
                        }
                    }
                    wrapper.data = wrapper.data.apply {
                        removeAll(toRemoveDisplay)
                        addAll(toRemoveDisplay.map { PlayerInfoData(it.profile, it.latency, it.gameMode, null) })
                    }
                }
            }
        })
    }

    val platform by lazy {
        ReplayPlugin.platform
    }

    @EventHandler
    fun onHandSwing(e: PlayerInteractEvent) {
        val heldItem = e.item ?: return
        val replaySession = e.player.world.replaySession ?: return
        val replayPlayer = e.player.replayUser
        val itemStack = heldItem.toReplayAction().takeIf { it.action != ControlItemAction.NONE } ?: return

        e.isCancelled = true
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            replaySession.playerStateHelper.handleItemSwing(replayPlayer, itemStack)
        } else if ((e.action == Action.RIGHT_CLICK_AIR || (e.action == Action.RIGHT_CLICK_BLOCK && e.interactionPoint != null))) {
            replaySession.playerStateHelper.handleItemAction(replayPlayer, itemStack.action)
        }
    }

}