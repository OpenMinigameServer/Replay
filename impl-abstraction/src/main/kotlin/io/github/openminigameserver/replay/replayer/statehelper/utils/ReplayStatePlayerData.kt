package io.github.openminigameserver.replay.replayer.statehelper.utils

import io.github.openminigameserver.replay.abstraction.ReplayGameMode
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.platform.ReplayPlatform


class ReplayStatePlayerData(
    private val isAllowFlying: Boolean,
    private val isFlying: Boolean,
    private val gameMode: ReplayGameMode,
    private val heldSlot: Byte,
    private val exp: Float,
    private val inventory: Any
) {

    constructor(replayPlatform: ReplayPlatform<*, ReplayUser, *>, player: ReplayUser) : this(
        player.isAllowFlying,
        player.isFlying,
        player.gameMode,
        player.heldSlot,
        player.exp,
        replayPlatform.getPlayerInventoryCopy(player)
    )

    fun apply(replayPlatform: ReplayPlatform<*, ReplayUser, *>, player: ReplayUser) {
        player.isAllowFlying = isAllowFlying
        player.isFlying = isFlying
        player.gameMode = gameMode
        player.setHeldItemSlot(heldSlot)
        player.exp = exp
        replayPlatform.loadPlayerInventoryCopy(player, inventory)
    }

}


