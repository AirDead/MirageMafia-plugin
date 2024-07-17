package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.player.privateWorld
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = true
        if (event.player.privateWorld != null && event.player.currentGame != null) {
            event.player.currentGame?.onBlockBreak(event.player, event.block)
        }
    }
}