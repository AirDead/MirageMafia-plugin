package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class FirstMate : Role {
    override val name = "Старпом"
    override val canRepair = true
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Бескозырка"),
        NamedItemStack(Material.STICK, "Пиратская сошка"),
        NamedItemStack(Material.WRITABLE_BOOK, "Деревянный планшет с бумагами"),
        NamedItemStack(Material.TOTEM_OF_UNDYING, "Карманные часы"),
    )
}