package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import java.util.*

class BlockListener(val app: ServerPlugin) : Listener {

    private val validPickaxes = EnumSet.of(
        Material.IRON_PICKAXE, Material.NETHERITE_PICKAXE, Material.GOLDEN_PICKAXE,
        Material.DIAMOND_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE
    )

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        event.isCancelled = true

        if (player.inventory.itemInMainHand.type !in validPickaxes) return

        val location = event.block.location
        val blockType = game.blockMap[location] ?: event.block.type

        app.server.scheduler.runTaskLater(app, Runnable {
            game.onBlockBreak(player, blockType, location)
        }, 4L)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.player.currentGame?.run {
            event.isCancelled = true
        }
    }
}
