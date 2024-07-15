package com.mirage.mafiagame.service

import com.mirage.mafiagame.Main
import com.mirage.mafiagame.game.MafiaGame
import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Player
import com.mirage.utils.models.Queue

class QueueService(val plugin: Main) {
    val queue1 = QueueManager {
        Queue(10) { players ->
            startGame(players)
        }
    }

    fun startGame(players: List<Player>) {
        val game = MafiaGame(plugin, players)
        game.start()
    }
}