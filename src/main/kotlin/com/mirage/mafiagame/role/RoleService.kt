package com.mirage.mafiagame.role

import dev.nikdekur.minelib.service.PluginService
import org.bukkit.entity.Player

interface RoleService : PluginService {
    fun assignRoles(players: List<Player>)
    fun getMafiaCount(playerCount: Int): Int
}
