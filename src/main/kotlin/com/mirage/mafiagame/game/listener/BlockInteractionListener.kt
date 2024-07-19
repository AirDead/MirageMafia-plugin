package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.isNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

object BlockInteractionListener : Listener {
    @EventHandler
    fun onBlockInteract(event: PlayerInteractEvent) {
        event.isCancelled = true
        val player = event.player
        val currentGame = player.currentGame
        if (currentGame.isNull()) return

        val inventory = currentGame?.chestInventories?.get(event.clickedBlock?.location)
        if (inventory != null) {
            player.openInventory(inventory)
        }
    }
}
