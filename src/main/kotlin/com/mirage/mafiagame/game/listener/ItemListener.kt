package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.nms.item.canBeTraded
import dev.nikdekur.minelib.v1_12_R1.ext.sendPackets
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class ItemListener : Listener {

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onTrade(event: PlayerInteractAtEntityEvent) {
        val player = event.player
        val target = event.rightClicked as? Player ?: return

        if (event.hand != EquipmentSlot.HAND) return

        when {
            target.isSneaking && !player.isSneaking -> handleItemInteraction(player, target, true)
            player.isSneaking && target.isSneaking -> handleItemInteraction(player, target, false)
        }
    }

    private fun handleItemInteraction(player: Player, target: Player, isStealing: Boolean) {
        val (source, destination) = if (isStealing) target to player else player to target
        val itemInHand = source.inventory.itemInMainHand

        if (!itemCanBeTraded(player, itemInHand, isStealing)) return

        destination.inventory.addItem(itemInHand)
        source.inventory.setItemInMainHand(null)

        val successMessage = if (isStealing) "Вы успешно выкрали предмет..." else "Вы успешно передали предмет..."
        player.sendActionBar(successMessage.asText(NamedTextColor.GREEN))

        if (!isStealing) target.sendActionBar("Вам передали предмет...".asText(NamedTextColor.GREEN))
        player.sendPackets()
    }

    private fun itemCanBeTraded(player: Player, item: ItemStack, isStealing: Boolean): Boolean {
        val action = if (isStealing) "выкрасть" else "передать"
        val errorMessage = when {
            item.type == Material.AIR -> "У вас нет предмета в руке..."
            !item.canBeTraded -> "Вы не можете $action этот предмет..."
            else -> null
        }
        errorMessage?.let {
            player.sendActionBar(it.asText(NamedTextColor.RED))
            return false
        }
        return true
    }
}