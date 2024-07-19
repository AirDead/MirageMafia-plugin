package com.mirage.mafiagame.game

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap

interface Game {
    val plugin: JavaPlugin
    val players: List<Player>
    val chestInventories: Map<Location, Inventory>
    val blockMap: ConcurrentHashMap<Location, Material>
    var brokenBlock: Int
    val completedTasks: MutableList<Int>
    var sabotageRunnable: BukkitTask?
    var lastSabotageEndTime: Long

    fun start()
    fun end()
    fun onMafiaKill(player: Player)
    fun onBlockBreak(player: Player, block: Block)
    fun onSabotageStart()
    fun onSabotageEnd(isRepaired: Boolean)
}