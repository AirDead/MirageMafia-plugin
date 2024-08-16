package com.mirage.mafiagame.config.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationConfig(
    val locations: List<LocationSetting> = listOf(LocationSetting())
)

@Serializable
data class LocationSetting(
    val type: LocationType = LocationType.SPAWN,
    val world: String = "world",
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
)
