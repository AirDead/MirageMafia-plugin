package com.mirage.mafiagame.extensions

import org.bukkit.Location

/**
 * Shifts the location vertically by a specified offset.
 *
 * @param yOffset The vertical offset to shift the location by. Positive values move the location up, while negative values move it down.
 * @return A new [Location] object with the y-coordinate adjusted by the specified offset.
 */
fun Location.shift(yOffset: Double): Location = clone().apply { y += yOffset }

/**
 * Extension property to get the location directly below the current one.
 */
val Location.below: Location
    get() = shift(-1.0)

/**
 * Extension property to get the location directly above the current one.
 */
val Location.above: Location
    get() = shift(1.0)