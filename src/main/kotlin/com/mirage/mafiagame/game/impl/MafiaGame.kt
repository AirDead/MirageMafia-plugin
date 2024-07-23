package com.mirage.mafiagame.game.impl

import com.github.retrooper.packetevents.util.Vector3i
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.isNotNull
import com.mirage.mafiagame.game.isNull
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.npc.Corpse
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
    override val chestInventories: Map<Location, Inventory>
) : Game {

    override val blockMap = ConcurrentHashMap<Location, Material>()
    override val updatedLocations = mutableSetOf<Vector3i>()
    override var brokenBlock: Int = 0
    override val completedTasks = mutableListOf<Int>()
    override var sabotageRunnable: BukkitTask? = null
    override var lastSabotageEndTime: Long = 0

    override fun start() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        onlinePlayers.forEach { player ->
            if (player in players) {
                player.apply {
                    teleport(Location(world, 196.0, 94.0, 1134.0)) // TODO: Set the map location
                    currentRole = Captain()
                    currentGame = this@MafiaGame
                    onlinePlayers.forEach { other -> if (other !in players) hidePlayer(plugin, other) }
                }
            } else {
                players.forEach { visiblePlayer -> visiblePlayer.hidePlayer(plugin, player) }
            }
        }
    }

    override fun end() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        onlinePlayers.forEach { player ->
            players.forEach { visiblePlayer -> player.showPlayer(plugin, visiblePlayer) }
        }
        players.forEach { player ->
            player.apply {
                teleport(world.spawnLocation)
                currentRole = null
                currentGame = null
                Corpse.clearAllCorpses(this)
                teleport(Location(Bukkit.getWorld("world"), 45.0, 28.0, 123.0))
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
        val location = player.location

        players.forEach {
            it.sendPackets(
                ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE),
                    createTabPacketInfo
                )
            )
            Corpse.spawnCorpse(it, "gosha", UUID.randomUUID(), location.x, location.y, location.z)
        }
    }

    override fun onBlockBreak(player: Player, block: Material, location: Location) {
        player.currentRole?.let { role ->
            when (block) {
                Material.YELLOW_CONCRETE -> {
                    if (sabotageRunnable.isNotNull() || System.currentTimeMillis() - lastSabotageEndTime < 5 * 60 * 1000 || !role.canBreak) return
                    updateBlock(location, Blocks.RED_CONCRETE.defaultBlockState())
                    if (++brokenBlock == 5) onSabotageStart()
                }
                Material.RED_CONCRETE -> {
                    if (sabotageRunnable.isNull() || !role.canRepair) return
                    updateBlock(location, Blocks.YELLOW_CONCRETE.defaultBlockState())
                    if (--brokenBlock == 0) {
                        onSabotageEnd(true)
                        sabotageRunnable?.cancel()
                    }
                }
                else -> {}
            }
        }
    }

    private fun updateBlock(location: Location, blockState: net.minecraft.world.level.block.state.BlockState) {
        val packet = ClientboundBlockUpdatePacket(location.toBlockPos(), blockState)
        players.forEach { it.sendPackets(packet) }
        blockMap[location] = if (blockState.block == Blocks.RED_CONCRETE) Material.RED_CONCRETE else Material.YELLOW_CONCRETE
        if (blockState.block == Blocks.RED_CONCRETE) updatedLocations.add(location.toVector3i()) else updatedLocations.remove(location.toVector3i())
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
                player.apply {
                    playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1))
                }
                end()
            }
        }
        lastSabotageEndTime = System.currentTimeMillis()
        brokenBlock = 0
    }
}