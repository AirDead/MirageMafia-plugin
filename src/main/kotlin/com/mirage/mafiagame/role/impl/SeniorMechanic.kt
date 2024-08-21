package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class SeniorMechanic : Role {
    override val name = "Старший механик"
    override val canRepair = true
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Сварочная маска"),
        NamedItemStack(Material.WOODEN_SWORD, "Разводной ключ"),
//        NamedItemStack(Material.SC, "Топор"),

    )
}