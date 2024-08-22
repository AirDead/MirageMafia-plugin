package com.mirage.mafiagame.chests

import com.mirage.mafiagame.config.chest.ChestConfig
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import org.bukkit.Location
import kotlin.reflect.KClass

class ConfigChestService(override val app: ServerPlugin) : ChestLocationService {
    override val bindClass: KClass<out Service<*>>
        get() = ChestLocationService::class

    lateinit var config: ChestConfig

    override fun onLoad() {
        config = app.loadConfig<ChestConfig>("chests")
        if (config.locations.isEmpty()) {
            app.logger.warning("No chests found in configuration.")
        } else {
            app.logger.info("Loaded ${config.locations.size} chests.")
        }
    }

    override fun getLocations(): List<Location> = config.locations.map { it.toBukkitLocation() }

}