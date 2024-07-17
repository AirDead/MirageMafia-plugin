package com.mirage.mafiagame.task.type

import com.mirage.mafiagame.task.Task
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface InventoryTask : Task {
    val inventory: Inventory
    fun onInventoryClick(player: Player, slot: Int)
}