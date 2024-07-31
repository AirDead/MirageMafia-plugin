package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class InteractionListener(app: JavaPlugin) : BaseModule<InteractionListener>(app), Listener {

    override fun onLoad() {
        app.server.pluginManager.registerEvents(this, app)
    }

    override fun onUnload() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
    }

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
            else -> {}
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.clickedInventory ?: return

        if (inventory.viewers.first().openInventory.title != "Голосование") return
        event.isCancelled = true

        val game = player.currentGame ?: return
        val clickedItem = event.currentItem ?: return

        when (clickedItem.type) {
            Material.BARRIER -> {
                if (game.skipVoters.add(player.uniqueId)) {
                    player.inventory.remove(Material.ENCHANTED_BOOK)
                    player.closeInventory()
                }
            }
            Material.PLAYER_HEAD -> {
                val target = clickedItem.itemMeta?.displayName ?: return
                if (game.kickVoters.add(player.uniqueId)) {
                    game.votingMap[target] = game.votingMap.getOrDefault(target, 0) + 1
                    player.inventory.remove(Material.ENCHANTED_BOOK)
                    player.closeInventory()
                }
            }

            else -> {}
        }
    }
}