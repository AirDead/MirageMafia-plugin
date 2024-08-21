package com.mirage.mafiagame.location

import com.mirage.mafiagame.config.location.LocationConfig
import com.mirage.mafiagame.config.location.LocationType
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.reflect.KClass

class LocationManagerService(override val app: ServerPlugin) : LocationService {
    override val bindClass: KClass<out Service<*>>
        get() = LocationService::class
    private val locations = hashMapOf<LocationType, Location>()

    override fun onLoad() {
        app.logger.info("Loading locations...")
        val config = app.loadConfig<LocationConfig>("locations")

        config.locations.forEach { setting ->
            val world = Bukkit.getWorld(setting.world)
            if (world != null) {
                locations[setting.type] = Location(world, setting.x, setting.y, setting.z)
                app.logger.info("Loaded location '${setting.type}' in world '${setting.world}'")
            } else {
                app.logger.warning("World '${setting.world}' not found for location '${setting.type}'")
            }
        }
    }

    override fun onUnload() {
        locations.clear()
    }

    override fun getLocation(type: LocationType): Location? = locations[type]

    override fun teleportPlayerToLocation(player: Player, type: LocationType): Boolean {
        val location = getLocation(type) ?: return false
        player.teleport(location)
        return true
    }
}