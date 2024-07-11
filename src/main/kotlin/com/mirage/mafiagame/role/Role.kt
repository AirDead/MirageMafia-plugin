package com.mirage.mafiagame.role

import org.bukkit.inventory.ItemStack

interface Role {
    val name: String
    val description: String
    val canRepair: Boolean
    val canBreak: Boolean
    val canKill: Boolean
    fun getInventory(): List<ItemStack>
}
