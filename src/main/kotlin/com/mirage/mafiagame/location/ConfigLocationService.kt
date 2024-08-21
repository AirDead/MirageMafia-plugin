package com.mirage.mafiagame.location

import com.mirage.mafiagame.config.location.LocationConfig
import com.mirage.mafiagame.config.location.LocationType
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.reflect.KClass

class ConfigLocationService(override val app: ServerPlugin) : LocationService {
    override val bindClass: KClass<out Service<*>>
        get() = LocationService::class

    lateinit var config: LocationConfig

    override fun onLoad() {
        app.logger.info("Loading locations...")

        config = app.loadConfig<LocationConfig>("locations")

        app.logger.info("Locations loaded.")
    }

    override fun getLocation(type: LocationType): Location? = when (type) {
        LocationType.GAME -> config.gameLocation.toBukkitLocation()
        LocationType.SPAWN -> config.spawnLocation.toBukkitLocation()
        LocationType.START_VOTING -> config.startVotingLocation.toBukkitLocation()
        LocationType.END_VOTING -> config.endVotingLocation.toBukkitLocation()
        else -> null
    }

    override fun teleportPlayerToLocation(player: Player, type: LocationType): Boolean {
        val location = getLocation(type) ?: return false
        player.teleport(location)
        return true
    }
}
