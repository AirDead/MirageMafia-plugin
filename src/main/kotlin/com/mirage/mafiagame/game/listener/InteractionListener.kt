package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object InteractionListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return

        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val player = event.player
        val game = player.currentGame

        when (clickedBlock.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                event.isCancelled = true
                game?.let {
                    val inventory = it.chestInventories[clickedBlock.location]
                    if (inventory != null) {
                        player.openInventory(inventory)
                    } else {
                        player.sendActionBar(Component.text("Заперто...").color(TextColor.color(200, 43, 43)))
                    }
                }
            }
            Material.RED_BED -> {
                event.isCancelled = true
                game?.onPlayerClickBed(player)
            }
            else -> {}
        }
    }
}