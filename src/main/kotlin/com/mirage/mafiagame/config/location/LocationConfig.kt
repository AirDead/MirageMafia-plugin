package com.mirage.mafiagame.config.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class LocationConfig(
    @SerialName("game_location")
    val gameLocation: LocationSetting = LocationSetting(),

    @SerialName("spawn_location")
    val spawnLocation: LocationSetting = LocationSetting(),

    @SerialName("start_voting_location")
    val startVotingLocation: LocationSetting = LocationSetting(),

    @SerialName("end_voting_location")
    val endVotingLocation: LocationSetting = LocationSetting()
)

@Serializable
data class LocationSetting(
    val world: String = "world",
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
) {
    fun toBukkitLocation() = Location(Bukkit.getWorld(world), x, y, z)
}
