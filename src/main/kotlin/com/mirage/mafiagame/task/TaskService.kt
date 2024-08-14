package com.mirage.mafiagame.task

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

interface TaskService {
    fun assignTask(player: Player, task: Task)
    fun canStartTask(player: Player, task: Task): Boolean
    fun clearAllTasks()
    fun onInventoryClick(event: InventoryClickEvent)
}
