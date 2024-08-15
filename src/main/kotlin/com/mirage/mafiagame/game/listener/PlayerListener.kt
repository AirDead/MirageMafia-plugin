package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.gameMap
import com.mirage.mafiagame.role.currentRole
import dev.nikdekur.minelib.plugin.ServerPlugin
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(val app: ServerPlugin) : Listener {

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

    private fun canPlayerKill(player: Player): Boolean {
        return player.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && player.currentRole?.canKill == true
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        hidePlayerFromAllGames(player)
    }

    private fun hidePlayerFromAllGames(player: Player) {
        gameMap.values.forEach { game ->
            game.players.forEach { it.hidePlayer(app, player); player.hidePlayer(app, it) }
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        player.teleport(Location(Bukkit.getWorld("world"), 157.0, 286.0, 127.0))
        player.inventory.clear()
        player.currentGame?.let { game ->
            game.killedPlayers.add(player)
            game.checkGameEnd()
        }
    }
}