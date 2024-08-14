package com.mirage.mafiagame.location

import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.reflect.KClass

class LocationManagerService(override val app: ServerPlugin) : LocationService, PluginService {
    override val bindClass: KClass<*>
        get() = LocationManagerService::class

    private val locations = hashMapOf<LocationType, Location>()

    override fun onLoad() {
        val config = app.loadConfig<LocationConfig>("locations")

        config.locations.forEach { setting ->
            val world: World? = Bukkit.getWorld(setting.world)
            if (world != null) {
                val location = Location(world, setting.x, setting.y, setting.z, setting.yaw, setting.pitch)
                locations[setting.type] = location
            } else {
                app.logger.warning("World '${setting.world}' not found for location '${setting.type}'")
            }
        }
    }

    override fun onUnload() {
        locations.clear()
    }

    override fun getLocation(type: LocationType): Location? {
        return locations[type]
    }

    override fun teleportPlayerToLocation(player: Player, type: LocationType): Boolean {
        val location = getLocation(type)
        return if (location != null) {
            player.teleport(location)
            true
        } else {
            false
        }
    }
}