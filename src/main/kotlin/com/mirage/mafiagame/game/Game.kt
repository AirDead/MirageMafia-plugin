package com.mirage.mafiagame.game

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

interface Game {
    val plugin: JavaPlugin
    val gameType: GameType
    val players: List<Player>
    val killedPlayers: MutableList<Player>

    fun start()
    fun stop()
    fun onMafiaKill(player: Player)
}
