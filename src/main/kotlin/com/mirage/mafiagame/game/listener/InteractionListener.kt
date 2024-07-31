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

        when (clickedBlock.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                event.isCancelled = true
                game.chestInventories[clickedBlock.location]?.let {
                    player.openInventory(it)
                } ?: player.sendActionBar(Component.text("Заперто...").color(TextColor.color(200, 43, 43)))
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

    private val prohibitedMaterials = setOf(
        Material.DARK_OAK_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
        Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR,
        Material.MANGROVE_TRAPDOOR, Material.CHERRY_TRAPDOOR, Material.BAMBOO_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR, Material.STONE_BUTTON,
        Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON,
        Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON,
        Material.MANGROVE_BUTTON, Material.CHERRY_BUTTON, Material.BAMBOO_BUTTON,
        Material.POLISHED_BLACKSTONE_BUTTON, Material.LEVER, Material.OAK_DOOR,
        Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
        Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.CRIMSON_DOOR,
        Material.WARPED_DOOR, Material.IRON_DOOR, Material.OAK_FENCE_GATE,
        Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
        Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.CRIMSON_FENCE_GATE,
        Material.WARPED_FENCE_GATE, Material.OAK_SIGN, Material.SPRUCE_SIGN,
        Material.BIRCH_SIGN, Material.JUNGLE_SIGN, Material.ACACIA_SIGN,
        Material.DARK_OAK_SIGN, Material.MANGROVE_SIGN, Material.CHERRY_SIGN,
        Material.BAMBOO_SIGN, Material.CRIMSON_SIGN, Material.WARPED_SIGN,
        Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
        Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
        Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX,
        Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX
    )

}