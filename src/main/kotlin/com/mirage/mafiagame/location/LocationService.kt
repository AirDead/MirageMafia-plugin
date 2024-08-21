package com.mirage.mafiagame.location

import com.mirage.mafiagame.config.location.LocationType
import dev.nikdekur.minelib.service.PluginService
import org.bukkit.Location
import org.bukkit.entity.Player

interface LocationService : PluginService {
    fun getLocation(type: LocationType): Location?
    fun teleportPlayerToLocation(player: Player, type: LocationType): Boolean
}