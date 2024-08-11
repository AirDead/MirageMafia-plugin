package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.config.ConfigService
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import dev.nikdekur.minelib.plugin.ServerPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.koin.core.component.inject

class InteractionListener(app: ServerPlugin) : Listener, BaseModule(app) {

    val storage by inject<ConfigService>()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        if (player.inventory.itemInMainHand.type == Material.ENCHANTED_BOOK) {
            if (game.skipVoters.contains(player.uniqueId)) {
                player.sendMessage("Вы уже проголосовали за скип")
                return
            }
            if (game.kickVoters.contains(player.uniqueId)) {
                player.sendMessage("Вы уже проголосовали за кик")
                return
            }
            game.openVotingMenu(player)
            return
        }

        val clickedBlock = event.clickedBlock ?: return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        event.isCancelled = true

        when (clickedBlock.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                game.chestInventories[clickedBlock.location]?.let {
                    player.openInventory(it)
                } ?: player.sendActionBar(Component.text("Заперто...").color(TextColor.color(200, 43, 43)))
            }
            Material.RED_BED -> {
                game.onPlayerClickBed(player, clickedBlock.location)
            }
            Material.STONE_BUTTON -> {
                val maxDistance = 3
                if (storage.meetingStartLocation.distance(player.location) <= maxDistance) {
                    game.startVoting(player)
                } else if (storage.meetingEndLocation.distance(player.location) <= maxDistance) {
                    game.endVoting()
                }
            }
            else -> {}
        }
    }
}