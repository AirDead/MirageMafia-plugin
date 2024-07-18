package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.role.Role
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Captain : Role {
    override val name: String = "Captain"
    override val canRepair: Boolean = true
    override val canBreak: Boolean = true

    override fun getInventory(): List<ItemStack> = listOf(
        ItemStack(Material.BOOK)
    )
}