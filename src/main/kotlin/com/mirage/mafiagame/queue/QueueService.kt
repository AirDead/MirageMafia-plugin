package com.mirage.mafiagame.queue

import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.mafiagame.module.BaseModule
import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Queue
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class QueueService(app: JavaPlugin) : BaseModule<QueueService>(app) {

    private val queues = mutableMapOf<QueueType, QueueManager>()

    override fun onLoad() {
        queues[QueueType.FIRST] = QueueManager { Queue(1) {
            MafiaGame(app, it.toBukkitPlayers().toMutableList()).start()
        } }

        queues[QueueType.SECOND] = QueueManager { Queue(15) {
            MafiaGame(app, it.toBukkitPlayers().toMutableList()).start()
        } }

        queues[QueueType.THIRD] = QueueManager { Queue(5) {
            MafiaGame(app, it.toBukkitPlayers().toMutableList()).start()
        } }
    }

    override fun onUnload() {
        queues.clear()
    }

    fun joinQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.addPlayerToQueue(player.toQueuePlayer())
    }

    fun removeQueue(player: Player, queueType: QueueType) {
        queues[queueType]?.removePlayerFromQueue(player.toQueuePlayer())
    }
}

fun Player.toQueuePlayer() = com.mirage.utils.models.Player(name)
fun List<com.mirage.utils.models.Player>.toBukkitPlayers() = mapNotNull { Bukkit.getPlayer(it.name) }

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

fun addPickaxesToInventories(inventories: Collection<Inventory>) {
    val pickaxeIndices = inventories.indices.shuffled().take(2)
    pickaxeIndices.forEach { index ->
        val pickaxe = ItemStack(Material.IRON_PICKAXE)
        inventories.elementAt(index).addItem(pickaxe)
    }
}