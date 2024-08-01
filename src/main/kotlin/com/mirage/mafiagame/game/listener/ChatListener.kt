package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class ChatListener(app: JavaPlugin) : BaseModule(app), Listener {

    override fun onLoad() {
        app.server.pluginManager.registerEvents(this, app)
    }

    override fun onUnload() {
        AsyncChatEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player

        if (player.currentGame != null) {
            event.viewers().removeIf { true }

            player.currentGame?.players?.forEach { gamePlayer ->
                event.viewers().add(gamePlayer)
            }
        } else {
            event.viewers().removeIf { true }

            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                if (onlinePlayer.currentGame == null) {
                    event.viewers().add(onlinePlayer)
                }
            }
        }
    }
}