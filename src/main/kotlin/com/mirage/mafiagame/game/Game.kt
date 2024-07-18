package com.mirage.mafiagame.game

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

interface Game {
    val plugin: JavaPlugin
    val players: List<Player>
    val completedTasks: MutableList<Int>
    var sabotageRunnable: BukkitTask?
    var brokenBlock: Int
    var lastSabotageEndTime: Long

    fun start()
    fun end()
    fun onMafiaKill(player: Player)
    fun onBlockBreak(player: Player, block: Block)
    fun onSabotageStart()
    fun onSabotageEnd(isRepaired: Boolean)
}
