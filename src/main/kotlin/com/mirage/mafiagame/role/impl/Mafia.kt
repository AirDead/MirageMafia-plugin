package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Mafia : Role {
    override val name = "Мафия"
    override val canRepair = false
    override val canBreak = true
    override val canKill = true

    override fun getInventory(): List<ItemStack> = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Маска мафии")
    )
}