package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

object InteractionListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
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


        when (clickedBlock.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                event.isCancelled = true
                game.let {
                    val inventory = it.chestInventories[clickedBlock.location]
                    if (inventory != null) {
                        player.openInventory(inventory)
                    } else {
                        player.sendActionBar(Component.text("Заперто...").color(TextColor.color(200, 43, 43)))
                    }
                }
            }
            Material.RED_BED -> {
                event.isCancelled = true
                game.onPlayerClickBed(player)
            }
            in prohibitedMaterials -> event.isCancelled = true
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

        if (clickedItem.type == Material.BARRIER) {
            if (!game.skipVoters.contains(player.uniqueId)) {
                game.skipVoters.add(player.uniqueId)
                player.inventory.remove(Material.ENCHANTED_BOOK)
                player.closeInventory()
            }
        } else if (clickedItem.type == Material.PLAYER_HEAD) {
            if (!game.kickVoters.contains(player.uniqueId)) {
                val target = clickedItem.itemMeta?.displayName ?: return
                game.votingMap[target] = game.votingMap.getOrDefault(target, 0) + 1
                game.kickVoters.add(player.uniqueId)
                player.inventory.remove(Material.ENCHANTED_BOOK)
                player.closeInventory()
            }
        }
    }

    val prohibitedMaterials = setOf(
        Material.DARK_OAK_TRAPDOOR,
        Material.OAK_TRAPDOOR,
        Material.SPRUCE_TRAPDOOR,
        Material.BIRCH_TRAPDOOR,
        Material.JUNGLE_TRAPDOOR,
        Material.ACACIA_TRAPDOOR,
        Material.MANGROVE_TRAPDOOR,
        Material.CHERRY_TRAPDOOR,
        Material.BAMBOO_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR,
        Material.WARPED_TRAPDOOR,
        Material.STONE_BUTTON,
        Material.OAK_BUTTON,
        Material.SPRUCE_BUTTON,
        Material.BIRCH_BUTTON,
        Material.JUNGLE_BUTTON,
        Material.ACACIA_BUTTON,
        Material.DARK_OAK_BUTTON,
        Material.MANGROVE_BUTTON,
        Material.CHERRY_BUTTON,
        Material.BAMBOO_BUTTON,
        Material.POLISHED_BLACKSTONE_BUTTON,
        Material.LEVER
    )
}