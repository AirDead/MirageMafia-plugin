package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import dev.nikdekur.minelib.plugin.ServerPlugin
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(app: ServerPlugin) : Listener, BaseModule(app) {

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