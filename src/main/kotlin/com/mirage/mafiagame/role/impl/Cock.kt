package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class Cock : Role {
    override val name = "Шеф-повар".asText()

    override val canRepair = false
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Поварской колпак"),
        NamedItemStack(Material.WOODEN_SWORD, "Кухонный нож"),
        NamedItemStack(Material.WOODEN_SWORD, "Скалка"),
        NamedItemStack(Material.WOODEN_SWORD, "Мачете"),
        NamedItemStack(Material.COOKED_PORKCHOP, 2, "Глазированная свинина"),
        NamedItemStack(Material.COOKED_COD, 4, "Рыбный батончик"),
        NamedItemStack(Material.BREAD, 2, "Галеты")
    )
}