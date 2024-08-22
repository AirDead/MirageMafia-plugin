package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.config.location.LocationType
import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.location.LocationService
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.service.PluginListener
import dev.nikdekur.ndkore.service.inject
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractionListener(override val app: ServerPlugin) : PluginListener {

    val location by inject<LocationService>()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        when {
            player.inventory.itemInMainHand.type == Material.ENCHANTED_BOOK ->
                handleEnchantedBookInteraction(player, game)

            event.action == Action.RIGHT_CLICK_BLOCK ->
                handleBlockInteraction(event, player, game)
        }
    }

    fun handleEnchantedBookInteraction(player: org.bukkit.entity.Player, game: Game) {
        when (player.uniqueId) {
            in game.skipVoters ->
                player.sendMessage("Вы уже проголосовали за скип")

            in game.kickVoters ->
                player.sendMessage("Вы уже проголосовали за кик")

            else ->
                game.openVotingMenu(player)
        }
    }

    fun handleBlockInteraction(event: PlayerInteractEvent, player: org.bukkit.entity.Player, game: Game) {
        val block = event.clickedBlock ?: return

        event.isCancelled = true

        when (block.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                val inventory = game.chestInventories[block.location]
                if (inventory != null) {
                    player.openInventory(inventory)
                } else {
                    player.sendActionBar("Заперто...".asText(200, 43, 43))
                }
            }
            Material.RED_BED -> {
                game.onPlayerClickBed(player, block.location)
                println("Player clicked bed")
            }

            Material.STONE_BUTTON ->
                handleButtonInteraction(player, game)

            else -> return
        }
    }

    fun handleButtonInteraction(player: org.bukkit.entity.Player, game: Game) {
        val maxDistance = 3.0

        when {
            location.getLocation(LocationType.START_VOTING)
                ?.distance(player.location)
                ?.let { it <= maxDistance } == true -> game.startVoting(player)

            location.getLocation(LocationType.END_VOTING)
                ?.distance(player.location)
                ?.let { it <= maxDistance } == true -> game.endVoting()
        }
    }
}
