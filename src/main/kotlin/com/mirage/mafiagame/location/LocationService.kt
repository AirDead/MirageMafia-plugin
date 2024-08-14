package com.mirage.mafiagame.location

import org.bukkit.Location
import org.bukkit.entity.Player

interface LocationService {
    fun getLocation(type: LocationType): Location?
    fun teleportPlayerToLocation(player: Player, type: LocationType): Boolean
}