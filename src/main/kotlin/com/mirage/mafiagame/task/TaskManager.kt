package com.mirage.mafiagame.task

import com.mirage.mafiagame.role.currentRole
import com.mirage.mafiagame.task.type.InventoryTask
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object TaskManager : Listener {
    val activeTask = mutableMapOf<Player, Task>()

    fun Player.startTask(task: Task) {
        if (isTaskAvailable(task)) {
            activeTask[this] = task
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val task = activeTask[player] as? InventoryTask ?: return
        task.onInventoryClick(player, event.slot)
    }

    fun Player.isTaskAvailable(task: Task): Boolean {
        return this.currentRole?.name in task.taskFor.map { it.name }
    }
}
