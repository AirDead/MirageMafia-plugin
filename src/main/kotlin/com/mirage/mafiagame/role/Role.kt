package com.mirage.mafiagame.role

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

interface Role {
    val name: Component
    val canRepair: Boolean
    val canBreak: Boolean
    val canKill: Boolean

    fun getInventory(): List<ItemStack>
}