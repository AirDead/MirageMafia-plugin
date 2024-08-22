package com.mirage.mafiagame.chests

import dev.nikdekur.minelib.service.PluginService
import org.bukkit.Location

interface ChestLocationService : PluginService {

    fun getLocations(): List<Location>
}