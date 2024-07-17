package com.mirage.mafiagame.game

import org.bukkit.entity.Player

private val gameMap = mutableMapOf<Player, Game?>()

var Player.currentGame: Game?
    get() = gameMap[this]
    set(value) {
        gameMap[this] = value
    }