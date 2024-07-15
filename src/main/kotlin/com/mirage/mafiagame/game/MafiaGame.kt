package com.mirage.mafiagame.game

import com.mirage.mafiagame.Main
import com.mirage.utils.models.Player

class MafiaGame(
    val plugin: Main,
    val players: List<Player>
) {
    val killedPlayers = mutableListOf<Player>()

    fun start() {
        // Логика начала игры
        players.forEach { player ->
            // Пример: отправка сообщения игрокам
            println(player.name)
        }
        // Дополнительная логика старта игры
    }
}