package com.mirage.mafiagame.extensions

import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.role.Role
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun List<Player>.teleportAll(location: Location) = forEach { it.teleport(location) }

fun List<Player>.setGame(game: Game?) = forEach { it.currentGame = game }

fun List<Player>.notifyAll(
    title: String,
    subtitle: String = "",
    fadeInSeconds: Int = 1,
    staySeconds: Int = 2,
    fadeOutSeconds: Int = 1
) = forEach {
    it.sendTitle(
        ChatColor.translateAlternateColorCodes('&', title),
        ChatColor.translateAlternateColorCodes('&', subtitle),
        fadeInSeconds * 20,
        staySeconds * 20,
        fadeOutSeconds * 20
    )
}

fun List<Player>.sendBossBar(
    message: String,
    color: BarColor = BarColor.WHITE,
    style: BarStyle = BarStyle.SOLID
) = Bukkit.createBossBar(
    ChatColor.translateAlternateColorCodes('&', message), color, style
).apply {
    isVisible = true
    forEach { addPlayer(it) }
}

private val playerGameMap = ConcurrentHashMap<UUID, Game?>()
private val playerRoleMap = ConcurrentHashMap<UUID, Role?>()

var Player.currentGame: Game?
    get() = playerGameMap[uniqueId]
    set(value) { playerGameMap[uniqueId] = value }

var Player.role: Role?
    get() = playerRoleMap[uniqueId]
    set(value) {
        playerRoleMap[uniqueId] = value
        sendMessage("Вам назначена роль: ${value?.name ?: "Ваша роль была сброшена."}")
        if (value == null) inventory.clear()
    }

fun String.getPlayerGameByName(name: String): Game? {
    val player = Bukkit.getPlayer(name)
    return player?.currentGame
}
