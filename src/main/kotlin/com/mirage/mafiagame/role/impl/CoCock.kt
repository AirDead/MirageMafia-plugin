package com.mirage.mafiagame.role.impl

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.role.Role
import org.bukkit.Material

class CoCock : Role {
    override val name = "Огузок".asText()

    override val canRepair = false
    override val canBreak = false
    override val canKill = false

    override fun getInventory() = listOf(
        NamedItemStack(Material.CARVED_PUMPKIN, "Венок жителя саванны"),
        NamedItemStack(Material.WOODEN_SWORD, "Кухонный нож"),
        NamedItemStack(Material.WOODEN_SWORD, "Скалка"),
        NamedItemStack(Material.IRON_SWORD, "Тесак"),
        NamedItemStack(Material.COOKED_COD, 2, "Рыбный батончик"),
        NamedItemStack(Material.BREAD, 4, "Галеты")
    )
}