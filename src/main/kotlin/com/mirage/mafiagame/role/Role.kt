package com.mirage.mafiagame.role

import org.bukkit.inventory.ItemStack

interface Role {
    val name: String
    val canRepair: Boolean
    val canBreak: Boolean

    fun getInventory(): List<ItemStack>
}