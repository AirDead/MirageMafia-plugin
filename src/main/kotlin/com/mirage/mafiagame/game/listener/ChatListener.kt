package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.service.PluginListener
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler

class ChatListener(override val app: ServerPlugin) : PluginListener {
    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player

        if (player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return
        }

        val game = player.currentGame
        event.viewers().clear()

        if (game != null) {
            event.viewers().addAll(game.players)
        } else {
            Bukkit.getOnlinePlayers().filter { it.currentGame == null }.forEach { event.viewers().add(it) }
        }
    }
}