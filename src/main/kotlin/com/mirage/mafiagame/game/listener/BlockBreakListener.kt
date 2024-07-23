package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class BlockBreakListener(val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = true

        val player = event.player
        val game = player.currentGame ?: return

        if (player.inventory.itemInMainHand.type != Material.IRON_PICKAXE) return

        val location = event.block.location
        val block = game.blockMap[location] ?: event.block.type

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            game.onBlockBreak(player, block, location)
        }, 4L)
    }
}