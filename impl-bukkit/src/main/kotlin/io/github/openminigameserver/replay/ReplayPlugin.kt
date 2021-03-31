package io.github.openminigameserver.replay

import io.github.openminigameserver.replay.platform.ReplayExtension
import io.github.openminigameserver.replay.platform.bukkit.BukkitReplayPlatform
import org.bukkit.plugin.java.JavaPlugin

class ReplayPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: ReplayPlugin
        lateinit var extension: ReplayExtension
        lateinit var platform: BukkitReplayPlatform
    }

    override fun onEnable() {
        instance = this
        platform = BukkitReplayPlatform()
        extension = ReplayExtension(platform)

        extension.init()
    }
}