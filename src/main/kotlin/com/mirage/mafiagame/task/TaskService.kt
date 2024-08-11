package com.mirage.mafiagame.task

import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.role.currentRole
import com.mirage.mafiagame.task.type.InventoryTask
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class TaskService(app: ServerPlugin) : BaseModule(app), Listener {
    val activeTasks = hashMapOf<Player, Task>()

    override fun onUnload() {
        activeTasks.clear()
    }

    fun Player.startTask(task: Task) {
        if (isTaskAvailable(task)) {
            activeTasks[this] = task
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val task = activeTasks[player] as? InventoryTask ?: return
        task.onInventoryClick(player, event.slot)
    }

    fun Player.isTaskAvailable(task: Task): Boolean {
        // TODO: Change complexity to O(1) from 2 x O(n)
        return this.currentRole?.name in task.taskFor.map { it.name }
    }
}