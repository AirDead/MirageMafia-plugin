package com.mirage.mafiagame.game.impl

import com.github.retrooper.packetevents.util.Vector3i
import com.mirage.mafiagame.config.ConfigService
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.module
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.nms.npc.Corpse
import com.mirage.mafiagame.queue.addPickaxesToInventories
import com.mirage.mafiagame.queue.generateRandomInventory
import com.mirage.mafiagame.role.RoleAssignService
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
    override val players: MutableList<Player>
) : Game {
    override val killedPlayers = mutableSetOf<Player>()
    override val chestInventories = mutableMapOf<Location, Inventory>()
    override val lastKillTime = mutableMapOf<String, Long>()
    override val updatedLocations = mutableSetOf<Vector3i>()
    override val completedTasks = mutableSetOf<Int>()
    override val blockMap = ConcurrentHashMap<Location, Material>()
    override var brokenBlock = 0
    override var sabotageRunnable: BukkitTask? = null
    override var timeRunnable: BukkitTask? = null
    override var nightSkipVotes = 0
    override var isNight = false
    override val bossBar = BossBar.bossBar(Component.text("Иконка солнца"), 1.0f, Color.GREEN, Overlay.PROGRESS)
    override var isVoting = false
    override val votingMap = mutableMapOf<String, Int>()
    override val skipVoters = mutableSetOf<UUID>()
    override val kickVoters = mutableSetOf<UUID>()

    val configService by module<ConfigService>()

    override fun start() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val assigner by module<RoleAssignService>()
        assigner.assignRoles(players)
        players.forEach { player ->
            player.currentGame = this
            player.teleport(configService.config.gameLocation)
            bossBar.let { player.showBossBar(it) }
            onlinePlayers.forEach {
                if (it !in players) {
                    player.hidePlayer(plugin, it)
                    it.hidePlayer(plugin, player)
                }
            }
        }

        fillChestInventories(configService.config.chestLocations.shuffled().take(10))
        startDayNightCycle()
    }


    override fun end(isMafiaWin: Boolean) {
        val onlinePlayers = Bukkit.getOnlinePlayers()

        val subInfo = if (isMafiaWin) {
            Component.text("Мафия победила", NamedTextColor.RED)
        } else {
            Component.text("Мирные победили", NamedTextColor.GREEN)
        }
        val title = Title.title(
            Component.text("Игра завершена", NamedTextColor.GREEN),
            subInfo,
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )
        players.forEach {
            onlinePlayers.forEach { player ->
                if (it.currentGame == player.currentGame) {
                    player.showPlayer(plugin, it)
                    it.showPlayer(plugin, player)
                } else if (player.currentGame == null) {
                    player.showPlayer(plugin, it)
                    it.showPlayer(plugin, player)
                } else {
                    player.hidePlayer(plugin, it)
                    it.hidePlayer(plugin, player)
                }
            }
            it.currentGame = null
            it.currentRole = null
            it.teleport(Location(Bukkit.getWorld("world"), 157.0, 286.0, 127.0))
            it.playSound(it.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
            it.showTitle(title)
            it.hideBossBar(bossBar)
            it.sendMessage(subInfo)
            it.inventory.clear()
            it.clearActivePotionEffects()
            Corpse.clearAllCorpses(it)
        }

        sabotageRunnable?.cancel()
        timeRunnable?.cancel()
        timeRunnable = null
        sabotageRunnable = null
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

        killedPlayers.add(player)
        checkGameEnd()
    }

    override fun onBlockBreak(player: Player, block: Material, location: Location) {
        when (block) {
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
                players.forEach {
                    it.sendPackets(packet)
                    it.sendActionBar(Component.text("Блок восстановлен, осталось: ${brokenBlock - 1}/5", NamedTextColor.GREEN))
                }
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
        val message = Component.text("Саботаж начался", NamedTextColor.RED)
        val title = Title.title(
            Component.text("САБОТАЖ", NamedTextColor.RED),
            Component.text("У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", NamedTextColor.WHITE),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )
        players.forEach {
            it.showTitle(title)
            it.sendMessage(message)
        }

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
            end(true)
            return
        }

        players.forEach {
            it.showTitle(title)
            it.sendMessage(Component.text("Саботаж устранён", NamedTextColor.GREEN))
        }

        sabotageRunnable?.cancel()  // Added to ensure the task is canceled
        sabotageRunnable = null
    }

    override fun startVoting(whoStarted: Player) {
        if (sabotageRunnable != null) {
           whoStarted.sendMessage(Component.text("Нельзя начать голосование во время саботажа", NamedTextColor.RED))
            return
        }
        if (isNight) {
            whoStarted.sendMessage(Component.text("Нельзя начать голосование ночью", NamedTextColor.RED))
            return
        }

        val message = Component.text("Голосование началось", NamedTextColor.RED)
        val title = Title.title(
            Component.text("ГОЛОСОВАНИЕ", NamedTextColor.RED),
            Component.text("Выберите кого вы хотите изгнать", NamedTextColor.WHITE),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )

        val item = NamedItemStack(Material.ENCHANTED_BOOK, "Голосование", false)

        players.forEach { player ->
            player.showTitle(title)
            player.sendMessage(message)
            player.inventory.addItem(item)
        }
        isVoting = true
    }

    override fun endVoting() {
        if (!isVoting) return

        val maxVotes = votingMap.values.maxOrNull()
        val skipVotes = skipVoters.size
        val totalVotes = votingMap.values.sum() + skipVotes

        val (message, title) = when {
            maxVotes == null || totalVotes == 0 -> "Голосование пропущено" to "Голосование пропущено"
            skipVotes >= maxVotes -> "Голосование пропущено" to "Голосование пропущено"
            votingMap.values.count { it == maxVotes } > 1 -> "Голосование не дало результата" to "Голосование не дало результата"
            else -> {
                val name = votingMap.entries.first { it.value == maxVotes }.key
                val player = Bukkit.getPlayer(name) ?: return
                onMafiaKill(player)
                "Игрок $name изгнан" to "Изгнан $name"
            }
        }

        val finalMessage = Component.text(message, if (maxVotes == null) NamedTextColor.GREEN else NamedTextColor.RED)
        val finalTitle = Title.title(
            Component.text("ГОЛОСОВАНИЕ", NamedTextColor.RED),
            Component.text(title, if (maxVotes == null) NamedTextColor.GREEN else NamedTextColor.RED),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )

        players.forEach {
            it.showTitle(finalTitle)
            it.sendMessage(finalMessage)
            it.inventory.remove(Material.ENCHANTED_BOOK)
        }

        if (maxVotes != null && votingMap.values.count { it == maxVotes } <= 1) {
            checkGameEnd()
        }

        votingMap.clear()
        skipVoters.clear()
        kickVoters.clear()

        isVoting = false
    }
    
    override fun openVotingMenu(player: Player) {
        val inventory = Bukkit.createInventory(player, 36, "Голосование")

        players.forEach {
            if (killedPlayers.contains(it)) return@forEach

            val item = NamedItemStack(Material.PLAYER_HEAD, it.name)
            inventory.addItem(item)
        }

        val barrier = NamedItemStack(Material.BARRIER, "Пропустить")

        inventory.addItem(barrier)

        player.openInventory(inventory)
    }

    override fun startDayNightCycle() {
        timeRunnable = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            startNight()
            }, 10 * 60 * 20) // 4 minutes
    }

    override fun startNight() {
        nightSkipVotes = 0
        isNight = true
        val title = Title.title(
            Component.text("Ночь наступила!").color(NamedTextColor.BLUE),
            Component.text("Иконка ночи").color(NamedTextColor.RED)
        )
        bossBar.name(Component.text("Иконка ночи"))
        bossBar.color(Color.RED)

        players.forEach { player ->
            player.showTitle(title)
            player.sendMessage(Component.text("Ночь наступила", NamedTextColor.BLUE))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 4 * 60, 4))
        }

        timeRunnable = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            endNight()
        }, 4 * 60 * 20) // 4 minutes
    }

    override fun endNight() {
        isNight = false
        fillChestInventories(chestInventories.keys.toList())
        val title = Title.title(
            Component.text("День наступил!").color(NamedTextColor.YELLOW),
            Component.text("Иконка солнца").color(NamedTextColor.GREEN)
        )

        bossBar.name(Component.text("Иконка солнца"))
        bossBar.color(Color.GREEN)

        players.forEach { player ->
            player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5 * 60, 1))
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            player.removePotionEffect(PotionEffectType.SLOW)
            player.showTitle(title)
            player.sendMessage(Component.text("День наступил", NamedTextColor.YELLOW))
            player.wakeup(false)
        }

        timeRunnable = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            startNight()
        }, 10 * 60 * 20)
    }

    override fun onPlayerClickBed(player: Player, location: Location) {
        if (!isNight) return
        val alive = players.size - killedPlayers.size
        nightSkipVotes++
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 4 * 60, 3))
        player.sleep(location, true)

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            if (nightSkipVotes > alive / 2) {
                endNight()
            }
        }, 10)
    }

    override fun checkGameEnd() {
        val alivePlayers = players.filter { !killedPlayers.contains(it) }
        val mafiaPlayers = alivePlayers.filter { it.currentRole?.canKill == true }
        val nonMafiaPlayers = alivePlayers.filter { it.currentRole?.canKill != true }

        if (mafiaPlayers.isEmpty()) {
            end(false)
        }
        else if(nonMafiaPlayers.isEmpty() || nonMafiaPlayers.size <= mafiaPlayers.size) {
            end(true)
        }
    }

    fun fillChestInventories(locations: List<Location>) {
        locations.forEach { location ->
            chestInventories[location] = generateRandomInventory().apply {
                addPickaxesToInventories(listOf(this))
            }
        }
    }
}
