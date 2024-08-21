package com.mirage.mafiagame.ext

fun Number.toInventorySize(): Int {
    val number = this.toInt()
    return when {
        number <= 9 -> 9
        number <= 18 -> 18
        number <= 27 -> 27
        number <= 36 -> 36
        number <= 45 -> 45
        number <= 54 -> 54
        else -> (number + 8) / 9 * 9
    }.coerceAtMost(54)
}
