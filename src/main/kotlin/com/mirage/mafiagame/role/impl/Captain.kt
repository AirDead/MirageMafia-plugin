package com.mirage.mafiagame.role.impl


import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Captain : Role {
    override val name = "Капитан"

    override val canRepair = true
    override val canBreak = false
    override val canKill = false

    override fun getInventory(): List<ItemStack> {
        return listOf(
            NamedItemStack(Material.CARVED_PUMPKIN, "Кепка капитана", false),
            NamedItemStack(Material.STICK, "Трость с черепом", false),
            NamedItemStack(Material.WRITABLE_BOOK, "Деревянный планшет с бумагами", false),
            NamedItemStack(Material.BREAD, 5, "Галеты", false)
        )
    }
}