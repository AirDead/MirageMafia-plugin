package com.mirage.mafiagame.manager

import com.mirage.mafiagame.Main
import com.mirage.mafiagame.game.GameType
//import com.mirage.mafiagame.module.MafiaModule
//import org.bukkit.Location
//
//class ConfigManager(app: Main) : MafiaModule(app) {
//    lateinit var gameTypes: List<GameType>
//    var lobbyLocation: Location? = null
//
//    override fun onLoad() {
//        app.configManager = this
//        lobbyLocation = app.config.getLocation("lobby")
//        gameTypes = app.config.getConfigurationSection("maps")?.getKeys(false)?.mapNotNull { key ->
//            app.config.getLocation("maps.$key")?.let { GameType(key, it) }
//        } ?: emptyList()
//    }
//}