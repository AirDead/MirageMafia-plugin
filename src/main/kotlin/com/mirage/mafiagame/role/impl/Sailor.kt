package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class Sailor : Role {
    override val name = "Матрос".asText()

    override val canRepair = false
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Шляпа из трюма"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Двууголка"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Треуголка"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Звезда Комсомола"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Пиратская бандана"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Оловянный солдатик"),
        NamedItemStack(Material.CARVED_PUMPKIN, "Пиратская шляпа"),
        NamedItemStack(Material.WOODEN_PICKAXE, "Стамеска "),
        NamedItemStack(Material.WOODEN_PICKAXE, "Строительный молоток"),
        NamedItemStack(Material.WOODEN_PICKAXE, "Киянка"),
        NamedItemStack(Material.STICK, "Костыль"),
        NamedItemStack(Material.IRON_SWORD, "Швабра"),
    )
}