package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.nms.item.canBeTraded
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

class ItemListener(app: JavaPlugin) : BaseModule<ItemListener>(app), Listener {

    override fun onLoad() {
        app.server.pluginManager.registerEvents(this, app)
    }

    override fun onUnload() {
        PlayerDropItemEvent.getHandlerList().unregister(this)
        PlayerInteractAtEntityEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onTrade(event: PlayerInteractAtEntityEvent) {
        val player = event.player
        val target = event.rightClicked as? Player ?: return

        if (event.hand != EquipmentSlot.HAND) return

        if (target.isSneaking && !player.isSneaking) {
            val targetItem = target.inventory.itemInMainHand

            if (targetItem.type == Material.AIR) {
                player.sendActionBar(Component.text("У игрока нет предмета в руке...").color(NamedTextColor.RED))
                return
            }

            if (!targetItem.canBeTraded) {
                player.sendActionBar(Component.text("Этот предмет нельзя выкрасть...").color(NamedTextColor.RED))
                return
            }

            player.inventory.addItem(targetItem)
            target.inventory.setItemInMainHand(null)
            player.sendActionBar(Component.text("Вы успешно выкрали предмет...").color(NamedTextColor.GREEN))
        } else if (player.isSneaking && target.isSneaking) {
            val playerItem = player.inventory.itemInMainHand

            if (playerItem.type == Material.AIR) {
                player.sendActionBar(Component.text("У вас нет предмета в руке...").color(NamedTextColor.RED))
                return
            }

            if (!playerItem.canBeTraded) {
                player.sendActionBar(Component.text("Этот предмет нельзя передать...").color(NamedTextColor.RED))
                return
            }

            target.inventory.addItem(playerItem)
            player.inventory.setItemInMainHand(null)
            player.sendActionBar(Component.text("Вы успешно передали предмет...").color(NamedTextColor.GREEN))
            target.sendActionBar(Component.text("Вам передали предмет...").color(NamedTextColor.GREEN))
        }
    }
}