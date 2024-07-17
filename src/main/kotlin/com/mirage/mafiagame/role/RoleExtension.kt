package com.mirage.mafiagame.role

import org.bukkit.entity.Player

private val roleMap = mutableMapOf<Player, Role?>()

var Player.currentRole: Role?
    get() = roleMap[this]
    set(value) {
        roleMap[this] = value
        if (value == null) this.inventory.clear()
    }