package com.mirage.mafiagame.game.listener


import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractionListener(override val app: ServerPlugin) : Listener, PluginService {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val game = player.currentGame ?: return

        when {
            player.inventory.itemInMainHand.type == Material.ENCHANTED_BOOK -> handleEnchantedBookInteraction(player, game)
            event.action == Action.RIGHT_CLICK_BLOCK -> handleBlockInteraction(event, player, game)
        }
    }

    private fun handleEnchantedBookInteraction(player: org.bukkit.entity.Player, game: Game) {
        when (player.uniqueId) {
            in game.skipVoters -> player.sendMessage("Вы уже проголосовали за скип")
            in game.kickVoters -> player.sendMessage("Вы уже проголосовали за кик")
            else -> game.openVotingMenu(player)
        }
    }

    private fun handleBlockInteraction(event: PlayerInteractEvent, player: org.bukkit.entity.Player, game: Game) {
        val block = event.clickedBlock ?: return

        event.isCancelled = true

        when (block.type) {
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL -> {
                game.chestInventories[block.location]?.let {
                    player.openInventory(it)
                } ?: player.sendActionBar("Заперто...".asText(200, 43, 43))
            }
            Material.RED_BED -> game.onPlayerClickBed(player, block.location)
            Material.STONE_BUTTON -> handleButtonInteraction(player, game)
            else -> return
        }
    }

    private fun handleButtonInteraction(player: org.bukkit.entity.Player, game: Game) {
        val maxDistance = 3.0
        // TODO: Implement this
        when {
//            storage.meetingStartLocation.distance(player.location) <= maxDistance -> game.startVoting(player)
//            storage.meetingEndLocation.distance(player.location) <= maxDistance -> game.endVoting()
        }
    }
}