package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.gameMap
import com.mirage.mafiagame.role.currentRole
import net.kyori.adventure.text.Component
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
import org.bukkit.plugin.java.JavaPlugin

class PlayerListener(
    val plugin: JavaPlugin
) : Listener {

    @EventHandler
    fun onPlayerAttackPlayer(event: EntityDamageByEntityEvent) {
        event.isCancelled = true

        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? Player ?: return

        if (attacker.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && attacker.currentRole?.canKill == true) {
            val lastKill = attacker.currentGame?.lastKillTime?.get(attacker.name) ?: 0
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastKill >= 60 * 1000) {
                attacker.currentGame?.onMafiaKill(victim)
                attacker.currentGame?.lastKillTime?.put(attacker.name, currentTime)
                attacker.sendActionBar(Component.text("Вы убили игрока!", NamedTextColor.RED))
            } else {
                attacker.sendActionBar(Component.text("Вы устали, не стоит так часто напрягаться!", NamedTextColor.YELLOW))
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        gameMap.forEach { (_, game) ->
            game.players.forEach {
                it.hidePlayer(plugin, player)
                player.hidePlayer(plugin, it)
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        val game = player.currentGame

        player.teleport(Location(Bukkit.getWorld("world"), 157.0, 286.0, 127.0))
        player.inventory.clear()
        game?.killedPlayers?.add(player)
        game?.checkGameEnd()
    }
}