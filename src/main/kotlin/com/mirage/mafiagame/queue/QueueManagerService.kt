@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.queue

import com.mirage.mafiagame.config.queue.QueueConfig
import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.mafiagame.models.impl.SingleQueue
import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class QueueManagerService(override val app: ServerPlugin) : QueueService, PluginService {
    override val bindClass: KClass<*> get() = QueueService::class

    private val queueManagers = mutableMapOf<String, QueueManager>()

    override fun onLoad() {
        app.logger.info("Loading queue managers...")
        val config = app.loadConfig<QueueConfig>("queue")

        if (config.queues.isEmpty()) {
            app.logger.warning("No queues found in configuration.")
            return
        }

        config.queues.forEach { queueConfig ->
            if (queueConfig.playerCount > 0) {
                queueManagers[queueConfig.name] = QueueManager {
                    SingleQueue(queueConfig.playerCount) { players ->
                        MafiaGame(app, players.convertToBukkitPlayers()).start()
                    }
                }
                app.logger.info("Queue manager for type '${queueConfig.name}' loaded successfully.")
            } else {
                app.logger.warning("Invalid queue configuration detected for queue '${queueConfig.name}'.")
            }
        }
    }

    override fun joinQueue(player: Player, queueType: String): Boolean {
        val queueManager = queueManagers[queueType] ?: return false
        val queuePlayer = player.convertToQueuePlayer()
        if (queueManager.isPlayerInAnyQueue(queuePlayer)) return false
        queueManager.addPlayerToQueue(queuePlayer)
        return true
    }

    override fun leaveQueue(player: Player, queueType: String): Boolean {
        val queueManager = queueManagers[queueType]
        val queuePlayer = player.convertToQueuePlayer()

        if (queueManager == null || !queueManager.isPlayerInAnyQueue(queuePlayer)) return false

        queueManager.removePlayerFromQueue(queuePlayer)
        return true
    }

    override fun getAvailableQueues(): List<String> = queueManagers.keys.toList()

    override fun isValidQueueType(queueType: String): Boolean = queueManagers.containsKey(queueType)
}

inline fun Player.convertToQueuePlayer() = com.mirage.mafiagame.models.Player(name)

inline fun List<com.mirage.mafiagame.models.Player>.convertToBukkitPlayers(): List<Player> =
    mapNotNull { Bukkit.getPlayer(it.name) }

fun generateRandomInventory(): Inventory {
    val inventory = Bukkit.createInventory(null, 9, Component.text("Эй, что тут у нас..."))
    repeat((0..5).random()) { inventory.addItem(ItemStack(Material.COOKED_BEEF)) }
    repeat((0..7).random()) { inventory.addItem(ItemStack(Material.BREAD)) }
    return inventory
}