package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.config.location.LocationType
import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.gameMap
import com.mirage.mafiagame.location.LocationService
import com.mirage.mafiagame.role.currentRole
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.service.PluginListener
import dev.nikdekur.ndkore.service.inject
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(override val app: ServerPlugin) : PluginListener {

    val location by inject<LocationService>()

    @EventHandler
    fun onPlayerAttackPlayer(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? Player ?: return

        event.isCancelled = true

        val game = attacker.currentGame ?: return

        if (canPlayerKill(attacker)) {
            val currentTime = System.currentTimeMillis()
            val lastKillTime = game.lastKillTime[attacker.name] ?: 0

            if (currentTime - lastKillTime >= 60 * 1000) {
                game.onMafiaKill(victim)
                game.lastKillTime[attacker.name] = currentTime
                attacker.sendActionBar("Вы убили игрока!".asText(NamedTextColor.RED))
            } else {
                attacker.sendActionBar("Вы устали, не стоит так часто напрягаться!".asText(NamedTextColor.YELLOW))
            }
        }
    }


    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val game = player.currentGame ?: return

        val inventoryName = event.view.title
        if (inventoryName != "Голосование") return

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

            else -> return
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        gameMap.values.forEach { game ->
            game.players.forEach { it.hidePlayer(app, player); player.hidePlayer(app, it) }
        }
        location.getLocation(LocationType.SPAWN)?.let { player.teleport(it) }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        location.getLocation(LocationType.SPAWN)?.let { player.teleport(it) }
        player.currentGame?.onMafiaKill(player)
    }

    fun canPlayerKill(player: Player) = player.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && player.currentRole?.canKill == true
}