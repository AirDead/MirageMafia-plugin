package com.mirage.mafiagame.task

import com.mirage.mafiagame.role.Role
import org.bukkit.entity.Player


interface Task {
    val id: Int
    val taskFor: List<Role>
    fun onTaskStart(player: Player)
    fun onTaskComplete(player: Player)
}