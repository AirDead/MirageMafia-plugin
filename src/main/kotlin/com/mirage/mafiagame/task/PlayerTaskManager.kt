package com.mirage.mafiagame.task

import com.mirage.mafiagame.role.currentRole
import com.mirage.mafiagame.task.type.InventoryTask
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.reflect.KClass

class PlayerTaskManager(override val app: ServerPlugin) : TaskService {
    override val bindClass: KClass<out Service<*>>
        get() = TaskService::class
    val playerActiveTasks = hashMapOf<Player, Task>()

    override fun onUnload() {
        clearAllTasks()
    }

    override fun assignTask(player: Player, task: Task) {
        if (canStartTask(player, task)) {
            playerActiveTasks[player] = task
        }
    }

    override fun canStartTask(player: Player, task: Task): Boolean {
        val taskForRolesSet = task.taskFor.map { it.name }.toSet()
        return player.currentRole?.name in taskForRolesSet
    }

    override fun clearAllTasks() {
        playerActiveTasks.clear()
    }

    @EventHandler
    override fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val task = playerActiveTasks[player] as? InventoryTask ?: return
        task.onInventoryClick(player, event.slot)
    }
}
