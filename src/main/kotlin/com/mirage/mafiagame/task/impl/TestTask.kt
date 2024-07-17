package com.mirage.mafiagame.task.impl

import com.mirage.mafiagame.role.Role
import com.mirage.mafiagame.role.impl.Captain
import com.mirage.mafiagame.task.type.InventoryTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class TestTask : InventoryTask {
    override val inventory: Inventory = Bukkit.createInventory(null, 9, "Test Task")
    override val taskFor: List<Role> = listOf(Captain())

    override fun onInventoryClick(player: Player, slot: Int) {
        if (slot == 0) {
            player.sendMessage("You clicked slot 0")
        }
    }


    override fun onTaskStart(player: Player) {
        player.sendMessage("Test task started")
    }

    override fun onTaskComplete(player: Player) {
        player.sendMessage("Test task completed")
    }
}