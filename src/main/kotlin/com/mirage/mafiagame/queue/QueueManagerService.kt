@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.queue

import com.mirage.mafiagame.config.queue.QueueConfig
import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Queue
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
    override val bindClass: KClass<*>
        get() = QueueService::class

    val queueManagers: MutableMap<String, QueueManager> = mutableMapOf()

    override fun onLoad() {
        val config = app.loadConfig<QueueConfig>("queue")

        config.queues.forEach { setting ->
            val queueManager = QueueManager {
                Queue(setting.playerCount) {
                    MafiaGame(app, it.convertToBukkitPlayers().toMutableList()).start()
                }
            }
            queueManagers[setting.type] = queueManager
        }
    }

    override fun joinQueue(player: Player, queueType: String) {
        queueManagers[queueType]?.addPlayerToQueue(player.convertToQueuePlayer())
    }

    override fun leaveQueue(player: Player, queueType: String) {
        queueManagers[queueType]?.removePlayerFromQueue(player.convertToQueuePlayer())
    }

    override fun getAvailableQueues(): List<String> {
        return queueManagers.keys.toList()
    }

    override fun isValidQueueType(queueType: String): Boolean {
        return queueManagers.containsKey(queueType)
    }
}

inline fun Player.convertToQueuePlayer() = com.mirage.utils.models.Player(name)
inline fun List<com.mirage.utils.models.Player>.convertToBukkitPlayers() = mapNotNull { Bukkit.getPlayer(it.name) }


fun generateRandomInventory(): Inventory {
    val inventory = Bukkit.createInventory(null, 9, Component.text("Эй, что тут у нас..."))

    val steakCount = (0..5).random()
    repeat(steakCount) {
        val steak = ItemStack(Material.COOKED_BEEF)
        inventory.addItem(steak)
    }

    val breadCount = (0..7).random()
    repeat(breadCount) {
        val bread = ItemStack(Material.BREAD)
        inventory.addItem(bread)
    }

    return inventory
}
