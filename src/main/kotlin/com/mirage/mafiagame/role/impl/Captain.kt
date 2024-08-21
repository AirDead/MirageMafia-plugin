package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Captain : Role {
    override val name = "Капитан".asText()

    override val canRepair = true
    override val canBreak = false
    override val canKill = false

    override fun getInventory(): List<ItemStack> {
        return listOf(
            NamedItemStack(Material.CARVED_PUMPKIN, "Кепка капитана", true),
            NamedItemStack(Material.STICK, "Трость с черепом", true),
            NamedItemStack(Material.WRITABLE_BOOK, "Деревянный планшет с бумагами", true),
            NamedItemStack(Material.BREAD, 5, "Галеты", true)
        )
    }
}