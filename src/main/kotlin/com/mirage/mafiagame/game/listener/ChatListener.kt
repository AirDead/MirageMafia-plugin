package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin

class ChatListener(app: JavaPlugin) : BaseModule(app), Listener {

    override fun onLoad() {
        app.server.pluginManager.registerEvents(this, app)
    }

    override fun onUnload() {
        AsyncChatEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val message = event.message
        val radius = 6.0

        event.recipients.clear()

        if (player.currentGame != null) {
            if (message.startsWith("!")) {
                player.currentGame?.players?.forEach { gamePlayer ->
                    event.recipients.add(gamePlayer)
                }
            } else {
                player.world.players.forEach { onlinePlayer ->
                    if (onlinePlayer.location.distance(player.location) <= radius) {
                        event.recipients.add(onlinePlayer)
                    }
                }
            }
        } else {
            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                if (onlinePlayer.currentGame == null) {
                    event.recipients.add(onlinePlayer)
                }
            }
        }
    }
}