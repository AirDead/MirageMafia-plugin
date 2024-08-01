package com.mirage.mafiagame.config

import com.mirage.mafiagame.module.BaseModule
import org.bukkit.plugin.java.JavaPlugin

class ConfigService(app: JavaPlugin) : BaseModule(app) {
    lateinit var config: MafiaConfig

    override fun onLoad() {
        if (!app.config.isSet("chests")) {
            app.saveResource("config.yml", false)
        }

        config = loadConfig(app.config)

        if (config.chests.isEmpty()) {
            app.logger.warning("No chests loaded from configuration.")
        } else {
            app.logger.info("${config.chests.size} chests loaded from configuration.")
        }

        if (config.tasksLocations.isEmpty()) {
            app.logger.warning("No task locations loaded from configuration.")
        } else {
            app.logger.info("${config.tasksLocations.size} task locations loaded from configuration.")
        }
    }

    override fun onUnload() {

    }
}