package com.mirage.mafiagame.nms.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

val CAN_BE_DROPPED_KEY = NamespacedKey("miragemafia", "can_be_dropped")

var ItemStack.canBeDropped: Boolean
    get() {
        val meta = this.itemMeta
        return meta?.persistentDataContainer?.get(CAN_BE_DROPPED_KEY, PersistentDataType.BYTE)?.toInt() != 0
    }
    set(value) {
        val meta = this.itemMeta
        meta?.persistentDataContainer?.set(CAN_BE_DROPPED_KEY, PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())
        this.itemMeta = meta
    }

class NamedItemStack : ItemStack {
    constructor(material: Material, name: String) : this(material, 1, name)

    constructor(material: Material, amount: Int, name: String) : super(material, amount) {
        val meta: ItemMeta? = itemMeta
        meta?.setDisplayName(name)
        itemMeta = meta
    }

    constructor(material: Material, amount: Int, name: String, canBeDropped: Boolean) : super(material, amount) {
        val meta: ItemMeta? = itemMeta
        meta?.setDisplayName(name)
        itemMeta = meta
        this.canBeDropped = canBeDropped
    }

    constructor(material: Material, name: String, canBeDropped: Boolean) : this(material, 1, name) {
        this.canBeDropped = canBeDropped
    }
}