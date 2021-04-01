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
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.PlayerInfoData

class WrapperPlayServerPlayerInfo : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer) : super(packet, TYPE)

    var action: PlayerInfoAction
        get() = handle.playerInfoAction.read(0)
        set(value) {
            handle.playerInfoAction.write(0, value)
        }
    var data: MutableList<PlayerInfoData>
        get() = handle.playerInfoDataLists.read(0)
        set(value) {
            handle.playerInfoDataLists.write(0, value)
        }

    companion object {
        val TYPE: PacketType = PacketType.Play.Server.PLAYER_INFO
    }
}