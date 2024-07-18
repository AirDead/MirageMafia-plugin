package com.mirage.mafiagame.role

import org.bukkit.entity.Player

private val roleMap = mutableMapOf<Player, Role>()

var Player.currentRole: Role?
    get() = roleMap[this]
    set(value) = if (value == null) {
        roleMap.remove(this)
        this.inventory.clear()
    } else {
        roleMap[this] = value
    }