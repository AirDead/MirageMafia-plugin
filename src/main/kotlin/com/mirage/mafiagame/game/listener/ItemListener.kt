package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.nms.item.canBeDropped
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object ItemListener : Listener {
    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        if (!event.itemDrop.itemStack.canBeDropped) {
            event.isCancelled = true
        }
    }
}