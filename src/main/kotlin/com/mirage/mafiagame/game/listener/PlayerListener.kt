package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.config.ConfigService
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.gameMap
import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.module.module
import com.mirage.mafiagame.role.currentRole
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerListener(app: JavaPlugin) : BaseModule(app), Listener {
    val storageService by module<ConfigService>()
    override fun onLoad() {
        app.server.pluginManager.registerEvents(this, app)
    }

    override fun onUnload() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
    }

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
                it.hidePlayer(app, player)
                player.hidePlayer(app, it)
            }
        }

        player.teleport(storageService.config.lobbyLocation)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        val game = player.currentGame

        player.teleport(storageService.config.lobbyLocation)
        game?.onMafiaKill(player)
        player.gameMode = GameMode.SURVIVAL
    }
}
