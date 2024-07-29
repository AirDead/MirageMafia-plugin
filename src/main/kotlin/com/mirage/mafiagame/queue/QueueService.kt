package com.mirage.mafiagame.queue

import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.utils.manager.QueueManager
import com.mirage.utils.models.Queue
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class QueueService(
    val plugin: JavaPlugin
) {
    val config = plugin.config

    val queues = mapOf(
        QueueType.FIRST to QueueManager { Queue(1) {
            val gameLocations = loadLocations(config).mapValues { generateRandomInventory() }.apply {
                addPickaxesToInventories(this.values)
            }
            MafiaGame(plugin, it.toBukkitPlayers(), gameLocations).start()
        } },
        QueueType.SECOND to QueueManager { Queue(15) {
            val gameLocations = loadLocations(config).mapValues { generateRandomInventory() }.apply {
                addPickaxesToInventories(this.values)
            }
            MafiaGame(plugin, it.toBukkitPlayers(), gameLocations).start()
        } },
        QueueType.THIRD to QueueManager { Queue(5) {
            val gameLocations = loadLocations(config).mapValues { generateRandomInventory() }.apply {
                addPickaxesToInventories(this.values)
            }
            MafiaGame(plugin, it.toBukkitPlayers(), gameLocations).start()
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

fun loadLocations(config: FileConfiguration): Map<Location, Inventory> {
    val locations = mutableMapOf<Location, Inventory>()
    val locSection = config.getConfigurationSection("blocks")

    val allLocations = mutableListOf<Location>()

    locSection?.getKeys(false)?.forEach { key ->
        val worldName = locSection.getString("$key.world")
        val world = Bukkit.getWorld(worldName ?: "world")
        val x = locSection.getDouble("$key.x")
        val y = locSection.getDouble("$key.y")
        val z = locSection.getDouble("$key.z")

        if (world != null) {
            val location = Location(world, x, y, z)
            allLocations.add(location)
        }
    }

    val selectedLocations = allLocations.shuffled().take(10)

    selectedLocations.forEach { location ->
        val inventory = generateRandomInventory()
        locations[location] = inventory
    }

    return locations
}

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