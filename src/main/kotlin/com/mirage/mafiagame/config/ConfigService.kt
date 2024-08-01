package com.mirage.mafiagame.config

import com.mirage.mafiagame.module.BaseModule
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class ConfigService(app: JavaPlugin) : BaseModule(app) {
    lateinit var config: MafiaConfig

    override fun onLoad() {
        // TODO: Load locations from config file

        val gameWorld = Bukkit.getWorld("game")
        val gameLocation = gameWorld?.spawnLocation ?: Location(Bukkit.getWorld("world"), 0.0, 65.0, 0.0)

        config = MafiaConfig(
            chestLocations = mutableListOf(),
            gameLocation = gameLocation,
            lobbyLocation = Location(Bukkit.getWorld("world"), 50.0, 65.0, 50.0),
            startMeetingLocation = Location(gameWorld, 203.0, 110.0, 4.0),
            endMeetingLocation = Location(gameWorld, 203.0, 110.0, 12.0),
            tasksLocations = mapOf()
        )
    }

    override fun onUnload() {
        config.chestLocations.clear()
    }
}
