package com.mirage.mafiagame.config.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationConfig(
    val locations: List<LocationSetting>
)

@Serializable
data class LocationSetting(
    val type: LocationType,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double
)
