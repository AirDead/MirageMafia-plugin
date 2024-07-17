package com.mirage.mafiagame.game

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

interface Game {
    val plugin: JavaPlugin
    val players: List<Player>
    var brokenBlock: Int
    val isSabotageStart: Boolean

    fun start()

    fun onMafiaKill(player: Player)

    fun onBlockBreak(player: Player, block: Block)

    fun onSabotageStart()

    fun onSabotageEnd(isRepaired: Boolean)
}