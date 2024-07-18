package com.mirage.mafiagame.game

import org.bukkit.entity.Player

private val gameMap = mutableMapOf<Player, Game>()

var Player.currentGame: Game?
    get() = gameMap[this]
    set(value) {
        if (value == null) {
            gameMap.remove(this)
        } else {
            gameMap[this] = value
        }
    }

fun Any?.isNotNull(): Boolean {
    return this != null
}

fun Any?.isNull(): Boolean {
    return this == null
}
