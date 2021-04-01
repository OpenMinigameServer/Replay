/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http:></http:>//dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package io.github.openminigameserver.replay.platform.bukkit.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.IntEnum
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import org.bukkit.ChatColor

class WrapperPlayServerScoreboardTeam : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer) : super(packet, TYPE) {}

    /**
     * Enum containing all known modes.
     *
     * @author dmulloy2
     */
    object Mode : IntEnum() {
        const val TEAM_CREATED = 0
        const val TEAM_REMOVED = 1
        const val TEAM_UPDATED = 2
        const val PLAYERS_ADDED = 3
        const val PLAYERS_REMOVED = 4
    }
    /**
     * Retrieve Team Name.
     *
     *
     * Notes: a unique name for the team. (Shared with scoreboard).
     *
     * @return The current Team Name
     */
    /**
     * Set Team Name.
     *
     * @param value - new value.
     */
    var name: String
        get() = handle.strings.read(0)
        set(value) {
            handle.strings.write(0, value)
        }
    /**
     * Retrieve Team Display Name.
     *
     *
     * Notes: only if Mode = 0 or 2.
     *
     * @return The current Team Display Name
     */
    /**
     * Set Team Display Name.
     *
     * @param value - new value.
     */
    var displayName: WrappedChatComponent
        get() = handle.chatComponents.read(0)
        set(value) {
            handle.chatComponents.write(0, value)
        }
    /**
     * Retrieve Team Prefix.
     *
     *
     * Notes: only if Mode = 0 or 2. Displayed before the players' name that are
     * part of this team.
     *
     * @return The current Team Prefix
     */
    /**
     * Set Team Prefix.
     *
     * @param value - new value.
     */
    var prefix: WrappedChatComponent
        get() = handle.chatComponents.read(1)
        set(value) {
            handle.chatComponents.write(1, value)
        }
    /**
     * Retrieve Team Suffix.
     *
     *
     * Notes: only if Mode = 0 or 2. Displayed after the players' name that are
     * part of this team.
     *
     * @return The current Team Suffix
     */
    /**
     * Set Team Suffix.
     *
     * @param value - new value.
     */
    var suffix: WrappedChatComponent
        get() = handle.chatComponents.read(2)
        set(value) {
            handle.chatComponents.write(2, value)
        }
    /**
     * Retrieve Name Tag Visibility.
     *
     *
     * Notes: only if Mode = 0 or 2. always, hideForOtherTeams, hideForOwnTeam,
     * never.
     *
     * @return The current Name Tag Visibility
     */
    /**
     * Set Name Tag Visibility.
     *
     * @param value - new value.
     */
    var nameTagVisibility: String
        get() = handle.strings.read(1)
        set(value) {
            handle.strings.write(1, value)
        }
    /**
     * Retrieve Color.
     *
     *
     * Notes: only if Mode = 0 or 2. Same as Chat colors.
     *
     * @return The current Color
     */
    /**
     * Set Color.
     *
     * @param value - new value.
     */
    var color: ChatColor
        get() = handle.getEnumModifier(ChatColor::class.java, MinecraftReflection.getMinecraftClass("EnumChatFormat"))
            .read(0)
        set(value) {
            handle.getEnumModifier(ChatColor::class.java, MinecraftReflection.getMinecraftClass("EnumChatFormat"))
                .write(0, value)
        }
    /**
     * Get the collision rule.
     * Notes: only if Mode = 0 or 2. always, pushOtherTeams, pushOwnTeam, never.
     * @return The current collision rule
     */
    /**
     * Sets the collision rule.
     * @param value - new value.
     */
    var collisionRule: String
        get() = handle.strings.read(2)
        set(value) {
            handle.strings.write(2, value)
        }
    /**
     * Retrieve Players.
     *
     *
     * Notes: only if Mode = 0 or 3 or 4. Players to be added/remove from the
     * team. Max 40 characters so may be uuid's later
     *
     * @return The current Players
     */
    /**
     * Set Players.
     *
     * @param value - new value.
     */
    var players: MutableList<String>
        get() = handle.getSpecificModifier(MutableCollection::class.java)
            .read(0) as MutableList<String>
        set(value) {
            handle.getSpecificModifier(MutableCollection::class.java).write(0, value)
        }
    /**
     * Retrieve Mode.
     *
     *
     * Notes: if 0 then the team is created. If 1 then the team is removed. If 2
     * the team team information is updated. If 3 then new players are added to
     * the team. If 4 then players are removed from the team.
     *
     * @return The current Mode
     */
    /**
     * Set Mode.
     *
     * @param value - new value.
     */
    var mode: Int
        get() = handle.integers.read(0)
        set(value) {
            handle.integers.write(0, value)
        }
    /**
     * Retrieve pack option data. Pack data is calculated as follows:
     *
     * <pre>
     * `
     * int data = 0;
     * if (team.allowFriendlyFire()) {
     * data |= 1;
     * }
     * if (team.canSeeFriendlyInvisibles()) {
     * data |= 2;
     * }
    ` *
    </pre> *
     *
     * @return The current pack option data
     */
    /**
     * Set pack option data.
     *
     * @param value - new value
     * @see .getPackOptionData
     */
    var packOptionData: Int
        get() = handle.integers.read(1)
        set(value) {
            handle.integers.write(1, value)
        }

    companion object {
        val TYPE = PacketType.Play.Server.SCOREBOARD_TEAM
    }
}