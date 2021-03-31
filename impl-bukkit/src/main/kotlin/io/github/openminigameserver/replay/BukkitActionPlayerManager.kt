package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayEntity
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayUser
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayWorld
import io.github.openminigameserver.replay.replayer.ActionPlayerManager

class BukkitActionPlayerManager(platform: ReplayPlatform<BukkitReplayWorld, BukkitReplayUser, BukkitReplayEntity>) :
    ActionPlayerManager<BukkitReplayWorld, BukkitReplayUser, BukkitReplayEntity>(
        platform
    ) {

}