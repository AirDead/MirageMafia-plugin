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
        with(event) {
            if (action != Action.RIGHT_CLICK_BLOCK || clickedBlock?.type !in listOf(Material.CHEST, Material.BARREL)) return
            isCancelled = true

            player.currentGame?.let { currentGame ->

                val text = Component.text("Бочка заперта...")
                    .color(TextColor.color(200, 43, 43))

                val inventory = currentGame.chestInventories[clickedBlock?.location]
                if (inventory != null) {
                    player.openInventory(inventory)
                } else {
                    player.sendActionBar(text)
                }
            }
        }
    }


}