package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class Detective : Role {
    override val name = "Детектив"
    override val canRepair = false
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Частный сыщик"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Трубка"),
        NamedItemStack(Material.BREAD, "Галеты"),
        NamedItemStack(Material.WRITABLE_BOOK, "Деревянный планшет с бумагами")
    )
}