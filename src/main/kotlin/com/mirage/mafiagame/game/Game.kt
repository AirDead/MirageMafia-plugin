package com.mirage.mafiagame.game

import com.github.retrooper.packetevents.util.Vector3i
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface Game {
    val plugin: JavaPlugin
    val players: MutableList<Player>
    val killedPlayers: MutableSet<Player>
    val chestInventories: Map<Location, Inventory>
    val blockMap: ConcurrentHashMap<Location, Material>
    var brokenBlock: Int
    var sabotageRunnable: BukkitTask?
    val updatedLocations: MutableSet<Vector3i>
    val completedTasks: MutableSet<Int>
    var nightSkipVotes: Int
    var timeRunnable: BukkitTask?
    var isNight: Boolean
    val bossBar: BossBar
    val lastKillTime: MutableMap<String, Long>
    var isVoting: Boolean
    val votingMap: MutableMap<String, Int>
    val skipVoters: MutableSet<UUID>
    val kickVoters: MutableSet<UUID>

    fun start()
    fun end(isMafiaWin: Boolean)
    fun onMafiaKill(player: Player)
    fun onBlockBreak(player: Player, block: Material, location: Location)
    fun onSabotageStart()
    fun onSabotageEnd(isRepaired: Boolean)
    fun startVoting(whoStarted: Player)
    fun endVoting()
    fun openVotingMenu(player: Player)
    fun startDayNightCycle()
    fun startNight()
    fun endNight()
    fun onPlayerClickBed(player: Player, location: Location)
    fun checkGameEnd()
}
