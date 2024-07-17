package com.mirage.mafiagame.queue

import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Queue
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class QueueService(
    val plugin: JavaPlugin
) {
    val queues = mapOf(
        QueueType.FIRST to QueueManager { Queue(5) { } },
        QueueType.SECOND to QueueManager { Queue(5) { } },
        QueueType.THIRD to QueueManager { Queue(5) {} },
    )

    fun joinQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.addPlayerToQueue(player.toQueuePlayer())
    }

    fun removeQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.removePlayerFromQueue(player.toQueuePlayer())
    }

}

fun Player.toQueuePlayer() = com.mirage.utils.models.Player(name)
fun List<com.mirage.utils.models.Player>.toBukkitPlayers() = map { Bukkit.getPlayer(it.name) }