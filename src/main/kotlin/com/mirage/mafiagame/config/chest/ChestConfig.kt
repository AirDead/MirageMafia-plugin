package com.mirage.mafiagame.config.chest

import com.mirage.mafiagame.config.location.LocationSetting
import kotlinx.serialization.SerialName

data class ChestConfig(
    @SerialName("chest_locations")
    val locations: List<LocationSetting> = emptyList()
)