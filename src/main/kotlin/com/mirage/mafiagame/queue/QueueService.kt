package com.mirage.mafiagame.queue

import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Queue
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class QueueService(
    val plugin: JavaPlugin
) {
    val testLocation = Location(Bukkit.getWorld("world"), 177.0, 63.0, 1137.0)
    val testInventory = Bukkit.createInventory(null, 9, "Test Inventory")

    val queues = mapOf(
        QueueType.FIRST to QueueManager { Queue(2) {
            MafiaGame(plugin, it.toBukkitPlayers(), mapOf(
                testLocation to testInventory
            )).start()
        } },
        QueueType.SECOND to QueueManager { Queue(15) {
            MafiaGame(plugin, it.toBukkitPlayers(), mapOf(
                testLocation to testInventory
            )).start()
        } },
        QueueType.THIRD to QueueManager { Queue(5) {
            MafiaGame(plugin, it.toBukkitPlayers(), mapOf(
                testLocation to testInventory
            )).start()
        } },
    )

    fun joinQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.addPlayerToQueue(player.toQueuePlayer())
    }

    fun removeQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.removePlayerFromQueue(player.toQueuePlayer())
    }

}

fun Player.toQueuePlayer() = com.mirage.utils.models.Player(name)
fun List<com.mirage.utils.models.Player>.toBukkitPlayers() = mapNotNull { Bukkit.getPlayer(it.name) }