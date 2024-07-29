package com.mirage.mafiagame.game.impl

import com.github.retrooper.packetevents.util.Vector3i
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.npc.Corpse
import com.mirage.mafiagame.role.RoleAssigner
import com.mirage.mafiagame.role.currentRole
import com.mirage.packetapi.extensions.craftPlayer
import com.mirage.packetapi.extensions.sendPackets
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
    override val completedTasks: MutableSet<Int> = mutableSetOf()
    override var brokenBlock: Int = 0
    override var sabotageRunnable: BukkitTask? = null
    override var nightSkipVotes = 0
    override var timeRunnable: BukkitTask? = null
    override val bossBar: BossBar = BossBar.bossBar(Component.text("Иконка солнца"), 1.0f, Color.GREEN, Overlay.PROGRESS)
    override val lastKillTime = mutableMapOf<UUID, Long>()

    override fun start() {
        RoleAssigner.assignRoles(players)
        players.forEach {
            it.currentGame = this
            it.teleport(Location(Bukkit.getWorld("game"), 167.0, 109.0, 8.0))
        }
        players.forEach { player ->
            bossBar.let { player.showBossBar(it) }
        }
        startDayNightCycle()
    }

    override fun end() {
        players.forEach {
            it.currentGame = null
            it.currentRole = null
            it.teleport(Location(Bukkit.getWorld("world"), 157.0, 286.0, 127.0))
        }
        val title = Title.title(
            Component.text("Игра завершенна", NamedTextColor.GREEN),
            Component.empty(),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )
        players.forEach { it.showTitle(title) }
        sabotageRunnable?.cancel()
        timeRunnable?.cancel()
        bossBar.let { bar ->
            players.forEach { player ->
                player.hideBossBar(bar)
            }
        }
    }

    override fun onMafiaKill(player: Player) {
        player.gameMode = GameMode.SPECTATOR
        val location = player.location
        players.forEach {
            it.sendPackets(
                ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE),
                    ClientboundPlayerInfoUpdatePacket.Entry(
                        player.uniqueId,
                        player.craftPlayer.profile,
                        true,
                        0,
                        GameType.SURVIVAL,
                        net.minecraft.network.chat.Component.literal(player.name),
                        null
                    )
                )
            )
            Corpse.spawnCorpse(it, "gosha", UUID.randomUUID(), location.x, location.y, location.z)
        }

        val nonMafiaPlayers = players.filter { it.currentRole?.canKill != true && it.gameMode != GameMode.SPECTATOR }
        if (nonMafiaPlayers.isEmpty()) {
            end()
        }
    }

    override fun onBlockBreak(player: Player, block: Material, location: Location) {
        when(block) {
            Material.YELLOW_CONCRETE -> {
                if (sabotageRunnable != null) return
                if (player.currentRole?.canBreak == false) return
                val packet = ClientboundBlockUpdatePacket(location.toBlockPos(), Blocks.RED_CONCRETE.defaultBlockState())
                players.forEach { it.sendPackets(packet) }
                updatedLocations.add(location.toVector3i())
                blockMap[location] = Material.RED_CONCRETE
                if (++brokenBlock == 5) onSabotageStart()
            }
            Material.RED_CONCRETE -> {
                if (sabotageRunnable == null) return
                if (player.currentRole?.canRepair == false) return
                updatedLocations.remove(location.toVector3i())
                val packet = ClientboundBlockUpdatePacket(location.toBlockPos(), Blocks.YELLOW_CONCRETE.defaultBlockState())
                players.forEach { it.sendPackets(packet) }
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
        val title = Title.title(
            Component.text("САБОТАЖ", NamedTextColor.RED),
            Component.text("У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", NamedTextColor.WHITE),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )
        players.forEach { it.showTitle(title) }

        sabotageRunnable = object : BukkitRunnable() {
            override fun run() = onSabotageEnd(false)
        }.runTaskLater(plugin, 20 * 300)
    }

    override fun onSabotageEnd(isRepaired: Boolean) {
        sabotageRunnable?.takeIf { !it.isCancelled } ?: return
        sabotageRunnable = null
        brokenBlock = 0

        val title = if (isRepaired) {
            Title.title(
                Component.text("САБОТАЖ УСПЕШНО УСТРАНЁН", NamedTextColor.GREEN),
                Component.empty(),
                Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
            )
        } else {
            players.forEach {
                it.playSound(it.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1))
            }
            end()
            return
        }
        players.forEach { it.showTitle(title) }
    }

    override fun startDayNightCycle() {
        timeRunnable = object : BukkitRunnable() {
            override fun run() {
                startNight()
            }
        }.runTaskLater(plugin, 10 * 60 * 20)
    }

    override fun startNight() {
        nightSkipVotes = 0

        val title = Title.title(
            Component.text("Ночь наступила!").color(NamedTextColor.BLUE),
            Component.text("Иконка ночи").color(NamedTextColor.RED)
        )
        bossBar?.name(Component.text("Иконка ночи"))
        bossBar?.color(Color.RED)

        players.forEach { player ->
            player.showTitle(title)
            bossBar?.let { player.showBossBar(it) }
        }

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            endNight()
        }, 4 * 60 * 20)
    }

    override fun startDay() {
        val title = Title.title(
            Component.text("День наступил!").color(NamedTextColor.YELLOW),
            Component.text("Иконка солнца").color(NamedTextColor.GREEN)
        )

        bossBar?.name(Component.text("Иконка солнца"))
        bossBar?.color(Color.GREEN)

        players.forEach { player ->
            player.showTitle(title)
            bossBar?.let { player.showBossBar(it) }
        }
    }

    override fun endNight() {
        players.forEach {
            it.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5 * 60, 1))
            it.removePotionEffect(PotionEffectType.BLINDNESS)
            it.removePotionEffect(PotionEffectType.SLOW)
        }
        startDay()
    }

    override fun onPlayerClickBed(player: Player) {
        nightSkipVotes++
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 4 * 60, 255))
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 4 * 60, 255))

        if (nightSkipVotes > players.size / 2) {
            endNight()
        }
    }
}