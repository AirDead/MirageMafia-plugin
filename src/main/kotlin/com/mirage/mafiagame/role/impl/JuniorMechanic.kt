package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class JuniorMechanic : Role {
    override val name = "Младший механик".asText()

    override val canRepair = true
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Очки бронника"),
        NamedItemStack(Material.IRON_SWORD, "Гаечный ключ"),
        NamedItemStack(Material.GOLDEN_PICKAXE, "Гаечный ключ"),
        NamedItemStack(Material.IRON_PICKAXE, "Молот"),
        NamedItemStack(Material.BREAD, "Галеты")
    )
}