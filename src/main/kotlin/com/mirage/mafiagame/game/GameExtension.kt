package com.mirage.mafiagame.game

import org.bukkit.entity.Player

val gameMap = mutableMapOf<Player, Game>()

var Player.currentGame: Game?
    get() = gameMap[this]
    set(value) {
        if (value == null) {
            gameMap.remove(this)
        } else {
            gameMap[this] = value
        }
    }