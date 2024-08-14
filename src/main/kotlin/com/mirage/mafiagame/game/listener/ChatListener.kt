package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(override val app: ServerPlugin) : Listener, PluginService {

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player
        val game = player.currentGame

        event.viewers().clear()

        if (game != null) {
            event.viewers().addAll(game.players)
        } else {
            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                if (onlinePlayer.currentGame == null) {
                    event.viewers().add(onlinePlayer)
                }
            }
        }
    }
}