package com.mirage.mafiagame.task

import dev.nikdekur.minelib.service.PluginService
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

interface TaskService : PluginService {
    fun assignTask(player: Player, task: Task)
    fun canStartTask(player: Player, task: Task): Boolean
    fun clearAllTasks()
    fun onInventoryClick(event: InventoryClickEvent)
}
