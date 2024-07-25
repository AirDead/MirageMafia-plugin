package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin

class BlockListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        event.isCancelled = true

        if (player.inventory.itemInMainHand.type != Material.IRON_PICKAXE) return

        val location = event.block.location
        val block = game.blockMap[location] ?: event.block.type

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            game.onBlockBreak(player, block, location)
        }, 4L)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (player.currentGame == null) return

        event.isCancelled = true
    }
}
