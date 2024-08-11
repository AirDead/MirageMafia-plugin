package com.mirage.mafiagame.role

import org.bukkit.entity.Player

interface RoleService {
    fun assignRoles(players: List<Player>)
    fun getMafiaCount(playerCount: Int): Int
}
