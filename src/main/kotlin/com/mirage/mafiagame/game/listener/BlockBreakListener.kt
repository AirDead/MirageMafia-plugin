package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class BlockBreakListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = true
        val player = event.player

        val game = player.currentGame ?: return
        val location = event.block.location

        val block = game.blockMap[location]?.let {
            event.block.apply { type = it }
        } ?: event.block

        player.sendMessage("$block")

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            game.onBlockBreak(player, block)
        }, 4)
    }
}
