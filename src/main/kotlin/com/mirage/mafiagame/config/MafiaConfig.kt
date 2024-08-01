package com.mirage.mafiagame.config

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

data class MafiaConfig(
    val chests: List<Location>,
    val gameLocation: Location,
    val lobbyLocation: Location,
    val startMeetingLocation: Location,
    val endMeetingLocation: Location,
    val tasksLocations: List<Pair<Location, String>>
)

fun loadConfig(config: FileConfiguration): MafiaConfig {
    fun getLocation(path: String): Location {
        return Location(
            Bukkit.getWorld(config.getString("$path.world")!!),
            config.getDouble("$path.x"),
            config.getDouble("$path.y"),
            config.getDouble("$path.z")
        )
    }

    val chests = config.getMapList("chests").mapNotNull { map ->
        val world = map["world"] as String?
        val x = (map["x"] as Number?)?.toDouble()
        val y = (map["y"] as Number?)?.toDouble()
        val z = (map["z"] as Number?)?.toDouble()

        if (world == null || x == null || y == null || z == null) {
            Bukkit.getLogger().warning("Invalid chest location configuration: $map")
            null
        } else {
            Location(Bukkit.getWorld(world), x, y, z)
        }
    }

    val tasksLocations = config.getMapList("tasksLocations").mapNotNull { map ->
        val world = map["location.world"] as String?
        val x = (map["location.x"] as Number?)?.toDouble()
        val y = (map["location.y"] as Number?)?.toDouble()
        val z = (map["location.z"] as Number?)?.toDouble()
        val task = map["task"] as String?

        if (world == null || x == null || y == null || z == null || task == null) {
            null
        } else {
            Pair(Location(Bukkit.getWorld(world), x, y, z), task)
        }
    }

    return MafiaConfig(
        chests,
        getLocation("gameLocation"),
        getLocation("lobbyLocation"),
        getLocation("startMeetingLocation"),
        getLocation("endMeetingLocation"),
        tasksLocations
    )
}