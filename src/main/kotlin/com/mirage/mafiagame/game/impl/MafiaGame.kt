package com.mirage.mafiagame.game.impl

import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.block.updatedLocations
import com.mirage.mafiagame.role.currentRole
import com.mirage.mafiagame.role.impl.Captain
import com.mirage.packetapi.extensions.craftPlayer
import com.mirage.packetapi.extensions.sendPackets
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import net.minecraft.world.level.block.Blocks
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MafiaGame(
    override val plugin: JavaPlugin,
    override val players: List<Player>,
    override val chestInventories: Map<Location, Inventory>,
) : Game {
    override val blockMap = ConcurrentHashMap<Location, Material>()
    override var brokenBlock: Int = 0
    override val completedTasks = mutableListOf<Int>()
    override var sabotageRunnable: BukkitTask? = null
    override var lastSabotageEndTime: Long = 0

    override fun start() {
        val onlinePlayers = Bukkit.getOnlinePlayers()

        onlinePlayers.forEach { player ->
            if (player in players) {
                player.teleport(Location(player.world, 196.0, 94.0, 1134.0)) // TODO: Установите местоположение карты
                player.currentRole = Captain()
                player.currentGame = this

                player.sendMessage(player.currentGame.toString() ?: "null")

                onlinePlayers.forEach { other ->
                    if (other !in players) {
                        player.hidePlayer(plugin, other)
                    }
                }
            } else {
                players.forEach { visiblePlayer ->
                    visiblePlayer.hidePlayer(plugin, player)
                }
            }
        }
    }

    override fun end() {
        val onlinePlayers = Bukkit.getOnlinePlayers()

        onlinePlayers.forEach { player ->
            players.forEach { visiblePlayer ->
                player.showPlayer(plugin, visiblePlayer)
            }
        }

        players.forEach { player ->
            player.teleport(player.world.spawnLocation)
            player.currentRole = null
            player.currentGame = null
            player.teleport(Location(Bukkit.getWorld("world"), 45.0, 28.0, 123.0))
            onlinePlayers.forEach { visiblePlayer ->
                player.showPlayer(plugin, visiblePlayer)
            }
        }
    }

    override fun onMafiaKill(player: Player) {
        val createTabPacketInfo = ClientboundPlayerInfoUpdatePacket.Entry(
            player.uniqueId,
            player.craftPlayer.profile,
            true,
            0,
            GameType.SURVIVAL,
            Component.literal(player.name),
            null
        )

        player.gameMode = GameMode.SPECTATOR

        players.forEach {
            it.sendPackets(ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), createTabPacketInfo))
            // TODO: Добавить спавн трупа
        }
    }

    override fun onBlockBreak(player: Player, block: Block) {
        val location = block.location
        when (block.type) {
            Material.YELLOW_CONCRETE -> {
                players.forEach {
                    it.sendPackets(
                        ClientboundBlockUpdatePacket(
                            location.toBlockPos(),
                            Blocks.RED_CONCRETE.defaultBlockState()
                        )
                    )
                }
                blockMap[location] = Material.RED_CONCRETE
                updatedLocations.add(location.toVector3i())
            }
            Material.RED_CONCRETE -> {
                updatedLocations.remove(location.toVector3i())
                players.forEach {
                    it.sendPackets(
                        ClientboundBlockUpdatePacket(
                            location.toBlockPos(),
                            Blocks.YELLOW_CONCRETE.defaultBlockState()
                        )
                    )
                }
                blockMap[location] = Material.YELLOW_CONCRETE
            }
            else -> {}
        }
    }

    override fun onSabotageStart() {
        players.forEach { it.sendTitle("САБОТАЖ.", "У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", 10, 70, 20) }
        sabotageRunnable = object : BukkitRunnable() {
            override fun run() = onSabotageEnd(false)
        }.runTaskLater(plugin, 20 * 300)
    }

    override fun onSabotageEnd(isRepaired: Boolean) {
        sabotageRunnable?.takeIf { !it.isCancelled } ?: return
        sabotageRunnable = null

        players.forEach { player ->
            if (isRepaired) {
                player.sendTitle("САБОТАЖ УСПЕШНО УСТРАНЁН", "", 10, 70, 20)
            } else {
                player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1))
                end()
            }
        }
        lastSabotageEndTime = System.currentTimeMillis()
    }
}
