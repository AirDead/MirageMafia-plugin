package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.BlockTypeStorage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class BlockBreakListener(
    val plugin: JavaPlugin
): Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = BlockTypeStorage.getBlock(event.block.location) ?: BlockTypeStorage.setBlock(event.block.location, event.block)
        with(event) {
            isCancelled = true
            player.currentGame?.let { game ->
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    game.onBlockBreak(player, block)
                }, 3)
            }
        }
    }
}