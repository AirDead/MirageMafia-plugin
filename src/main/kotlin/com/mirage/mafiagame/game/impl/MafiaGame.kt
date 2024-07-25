package com.mirage.mafiagame.game.impl

import com.github.retrooper.packetevents.util.Vector3i
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.isNotNull
import com.mirage.mafiagame.game.isNull
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.npc.Corpse
import com.mirage.mafiagame.role.RoleAssigner
import com.mirage.mafiagame.role.currentRole
import com.mirage.packetapi.extensions.craftPlayer
import com.mirage.packetapi.extensions.sendPackets
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
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
import java.time.Duration
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
        RoleAssigner.assignRoles(players)

        onlinePlayers.forEach { player ->
            if (player.currentGame == null || player.currentGame != this) {
                players.forEach { hiddenPlayer ->
                    player.hidePlayer(plugin, hiddenPlayer)
                }
            }
        }

        players.forEach { player ->
            player.currentGame = this
        }
    }

    override fun end() {
        val onlinePlayers = Bukkit.getOnlinePlayers()

        players.forEach { player ->
            player.currentGame = null
            player.currentRole = null
        }

        onlinePlayers.forEach { player ->
            onlinePlayers.forEach { otherPlayer ->
                if (player != otherPlayer) {
                    if (player.currentGame == null && otherPlayer.currentGame == null) {
                        player.showPlayer(plugin, otherPlayer)
                    } else if (player.currentGame != otherPlayer.currentGame) {
                        player.hidePlayer(plugin, otherPlayer)
                    } else {
                        player.showPlayer(plugin, otherPlayer)
                    }
                }
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
            net.minecraft.network.chat.Component.literal(player.name),
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
        when (block) {
            Material.YELLOW_CONCRETE -> {
                if (player.currentRole?.canBreak == false) return
                if (sabotageRunnable.isNotNull() || System.currentTimeMillis() - lastSabotageEndTime < 5 * 60 * 1000) return
                val packet = ClientboundBlockUpdatePacket(location.toBlockPos(), Blocks.RED_CONCRETE.defaultBlockState())
                players.forEach { it.sendPackets(packet) } // Packet yellow -> red
                updatedLocations.add(location.toVector3i()) // Add for block update listener
                blockMap[location] = Material.RED_CONCRETE
                if (++brokenBlock == 5) {
                    onSabotageStart()
                }
            }
            Material.RED_CONCRETE -> {
                if (player.currentRole?.canRepair == false) return
                if (sabotageRunnable.isNull()) return
                updatedLocations.remove(location.toVector3i()) // Remove for block update listener
                val packet = ClientboundBlockUpdatePacket(location.toBlockPos(), Blocks.YELLOW_CONCRETE.defaultBlockState())
                players.forEach { it.sendPackets(packet) } // Packet red -> yellow
                blockMap[location] = Material.YELLOW_CONCRETE
                if (--brokenBlock == 0) {
                    onSabotageEnd(true)
                    sabotageRunnable?.cancel()
                }
            }
            else -> {}
        }
    }

    override fun onSabotageStart() {
        val topTitle = Component.text("САБОТАЖ", NamedTextColor.RED)
        val subTitle = Component.text("У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", TextColor.color(255, 255, 255))
        val time =  Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))

        val title = Title.title(topTitle, subTitle, time)

        players.forEach { player ->
            player.showTitle(title)
        }

        sabotageRunnable = object : BukkitRunnable() {
            override fun run() = onSabotageEnd(false)
        }.runTaskLater(plugin, 20 * 300)
    }

    override fun onSabotageEnd(isRepaired: Boolean) {
        sabotageRunnable?.takeIf { !it.isCancelled } ?: return
        sabotageRunnable = null

        val repairedTitle = Title.title(
            Component.text("САБОТАЖ УСПЕШНО УСТРАНЁН", NamedTextColor.GREEN),
            Component.empty(),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )

        players.forEach { player ->
            if (isRepaired) {
                player.showTitle(repairedTitle)
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