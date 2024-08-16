@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.queue

import com.mirage.mafiagame.config.queue.QueueConfig
import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.mafiagame.models.impl.SingleQueue
import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class QueueManagerService(override val app: ServerPlugin) : QueueService, PluginService {
    override val bindClass: KClass<*> get() = QueueService::class

    val queueManagers = mutableMapOf<String, QueueManager>()

    override fun onLoad() {
        app.logger.info("Loading queue managers...")

        val config = app.loadConfig<QueueConfig>("queue")

        if (config.queues.isEmpty()) {
            app.logger.warning("No queues found in configuration.")
            return
        }

        config.queues.forEach {
            if (it.playerCount > 0) {
                queueManagers[it.name] = QueueManager {
                    SingleQueue(it.playerCount) { players ->
                        MafiaGame(app, players.convertToBukkitPlayers()).start()
                    }
                }
                app.logger.info("Queue manager for type '${it.name}' loaded successfully.")
            } else {
                app.logger.warning("Invalid queue configuration detected for queue '${it.name}'.")
            }
        }
    }

    override fun joinQueue(player: Player, queueType: String) {
        val queueManager = queueManagers[queueType]
        if (queueManager != null) {
            val queuePlayer = player.convertToQueuePlayer()
            if (queueManager.isPlayerInAnyQueue(queuePlayer)) {
                player.sendMessage("§cВы уже находитесь в очереди типа '$queueType'.".asText(NamedTextColor.RED))
                return
            }
            queueManager.addPlayerToQueue(queuePlayer)
        } else {
            player.sendMessage("§cТип очереди '$queueType' не существует.".asText(NamedTextColor.RED))
        }
    }

    override fun leaveQueue(player: Player, queueType: String) {
        val queueManager = queueManagers[queueType]
        if (queueManager != null) {
            val queuePlayer = player.convertToQueuePlayer()
            if (!queueManager.isPlayerInAnyQueue(queuePlayer)) {
                player.sendMessage("§cВы не находитесь в очереди типа '$queueType'.".asText(NamedTextColor.RED))
                return
            }
            queueManager.removePlayerFromQueue(queuePlayer)
        } else {
            app.logger.warning("Queue type '$queueType' does not exist. Player ${player.name} could not leave.")
        }
    }

    override fun getAvailableQueues(): List<String> = queueManagers.keys.toList()

    override fun isValidQueueType(queueType: String): Boolean = queueManagers.containsKey(queueType)
}

inline fun Player.convertToQueuePlayer() = com.mirage.mafiagame.models.Player(name)
inline fun List<com.mirage.mafiagame.models.Player>.convertToBukkitPlayers(): List<Player> {
    return mapNotNull { player ->
        val bukkitPlayer = Bukkit.getPlayer(player.name)
        if (bukkitPlayer == null) {
            Bukkit.getLogger().warning("Player '${player.name}' is not online.")
        }
        bukkitPlayer
    }
}

fun generateRandomInventory(): Inventory {
    val inventory = Bukkit.createInventory(null, 9, Component.text("Эй, что тут у нас..."))
    repeat((0..5).random()) { inventory.addItem(ItemStack(Material.COOKED_BEEF)) }
    repeat((0..7).random()) { inventory.addItem(ItemStack(Material.BREAD)) }
    return inventory
}
