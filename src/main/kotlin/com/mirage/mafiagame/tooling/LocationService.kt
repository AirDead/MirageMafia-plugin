@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.tooling

import com.mirage.mafiagame.module.BaseModule
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class LocationService(app: JavaPlugin) : BaseModule<LocationService>(app) {
    val chestLocations = mutableListOf<Location>()
    lateinit var gameLocation: Location
    lateinit var lobbyLocation: Location

    override fun onLoad() {
        app.config.run {
            // TODO: Make loading from config
            chestLocations.addAll(loadLocations(this))
            loadGameAndLobbyLocations(this)
        }
    }

    override fun onUnload() {
        chestLocations.clear()
    }

    fun loadLocations(config: FileConfiguration): List<Location> {
        return emptyList()
    }

    inline fun loadGameAndLobbyLocations(config: FileConfiguration) {
        gameLocation = Location(app.server.getWorld("world"), 0.0, 0.0, 0.0)
        lobbyLocation = Location(app.server.getWorld("world"), 0.0, 0.0, 0.0)
    }
}