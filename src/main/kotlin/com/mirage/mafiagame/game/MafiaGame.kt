package com.mirage.mafiagame.game

import com.mirage.mafiagame.Main
import com.mirage.mafiagame.extensions.below
import com.mirage.mafiagame.extensions.currentGame
import dev.geco.gsit.api.GSitAPI
import org.bukkit.entity.Player
import org.bukkit.entity.Pose

class MafiaGame(
    override val plugin: Main,
    override val gameType: GameType,
    override val players: List<Player>
) : Game {
    override val killedPlayers = mutableListOf<Player>()

    override fun start() {
        players.forEach { player ->
            player.currentGame = this
            player.teleport(gameType.gameLocation)
        }
    }

    override fun stop() {
//        val lobbyLocation = plugin.configManager.lobbyLocation
//        players.forEach { player ->
//            player.currentGame = null
//            lobbyLocation?.let { player.teleport(it) }
//        }
    }

    override fun onMafiaKill(player: Player) {
        if (player !in killedPlayers) {
            GSitAPI.createPose(player.location.below.block, player, Pose.SLEEPING)
            killedPlayers.add(player)
        }
    }
}