package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener(app: ServerPlugin) : BaseModule(app), Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        event.isCancelled = true

        if (player.inventory.itemInMainHand.type !in listOf(
                Material.IRON_PICKAXE, Material.NETHERITE_PICKAXE, Material.GOLDEN_PICKAXE,
                Material.DIAMOND_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE
            )) {
            return
        }

        val location = event.block.location
        val block = game.blockMap[location] ?: event.block.type

        app.server.scheduler.runTaskLater(app, Runnable {
            game.onBlockBreak(player, block, location)
        }, 4L)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        player.currentGame?.let {
            event.isCancelled = true
        }
    }
}