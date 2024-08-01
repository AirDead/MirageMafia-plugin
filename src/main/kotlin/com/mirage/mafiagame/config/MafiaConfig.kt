package com.mirage.mafiagame.config

import org.bukkit.Location

data class MafiaConfig(
    val chestLocations: MutableList<Location>,
    val gameLocation: Location,
    val lobbyLocation: Location
)