package com.mirage.mafiagame.game.impl

import com.github.retrooper.packetevents.util.Vector3i
import com.mirage.mafiagame.config.location.LocationType
import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.ext.craftPlayer
import com.mirage.mafiagame.ext.sendPackets
import com.mirage.mafiagame.ext.toInventorySize
import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.location.LocationService
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.item.NamedItemStack
import com.mirage.mafiagame.nms.npc.Corpse
import com.mirage.mafiagame.queue.generateRandomInventory
import com.mirage.mafiagame.role.RoleAssignmentService
import com.mirage.mafiagame.role.currentRole
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.service.PluginComponent
import dev.nikdekur.ndkore.service.inject
import net.kyori.adventure.bossbar.BossBar
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
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

class MafiaGame(
    override val app: ServerPlugin,
    override val players: List<Player>
) : Game, PluginComponent {
    private val locationConfig by inject<LocationService>()
    private val roleAssignService by inject<RoleAssignmentService>()

    override val killedPlayers = mutableSetOf<Player>()
    override val chestInventories = mutableMapOf<Location, Inventory>()
    override val lastKillTime = mutableMapOf<String, Long>()
    override val updatedLocations = mutableSetOf<Vector3i>()
    override val completedTasks = mutableSetOf<Int>()
    override val blockMap = ConcurrentHashMap<Location, Material>()
    override var brokenBlock = 0
    override var sabotageRunnable: BukkitTask? = null
    override var timeRunnable: BukkitTask? = null
    override var sleepingPlayers = mutableSetOf<UUID>()
    override var isNight = false
    override var dayCount = 1
    override val bossBar = BossBar.bossBar("День | $dayCount".asText(), 1.0f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS)
    override var isVoting = false
    override val votingMap = mutableMapOf<String, Int>()
    override val skipVoters = mutableSetOf<UUID>()
    override val kickVoters = mutableSetOf<UUID>()

    override fun start() {
        roleAssignService.assignRoles(players)
        val location = locationConfig.getLocation(LocationType.GAME) ?: error("Game location not found")
        players.forEach { player ->
            player.currentGame = this
            player.teleport(location)
            
            bossBar.let { player.showBossBar(it) }
            Bukkit.getOnlinePlayers().forEach {
                if (it !in players) {
                    player.hidePlayer(app, it)
                    it.hidePlayer(app, player)
                }
            }
        }

//        fillChestInventories(locationConfig.chestLocations.shuffled().take(10))
        // TODO: Fix
        startDayNightCycle()
    }

    override fun end(isMafiaWin: Boolean) {
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
            Bukkit.getOnlinePlayers().forEach { player ->
                if (it.currentGame == player.currentGame || it.currentGame == null) {
                    player.showPlayer(app, it)
                    it.showPlayer(app, player)
                } else {
                    player.hidePlayer(app, it)
                    it.hidePlayer(app, player)
                }   
            }
            it.currentGame = null
            it.currentRole = null
            it.teleport(locationConfig.getLocation(LocationType.SPAWN) ?: error("Spawn location not found"))
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
        player.inventory.clear()
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
                    it.sendActionBar(Component.text("Починенно ${brokenBlock - 1}/5", NamedTextColor.GREEN))
                }
                blockMap[location] = Material.YELLOW_CONCRETE
                if (--brokenBlock == 0) {
                    onSabotageEnd(true)
                    sabotageRunnable?.cancel()
                }
            }

            else -> return
        }
    }

    override fun onSabotageStart() {
        val message = "Произошла поломка, скорее устроните ее".asText()
        val title = Title.title(
            Component.text("ПОЛОМКА", NamedTextColor.RED),
            Component.text("У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", NamedTextColor.WHITE),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )
        players.forEach {
            it.showTitle(title)
            it.sendMessage(message)
        }

        sabotageRunnable = object : BukkitRunnable() {
            override fun run() = onSabotageEnd(false)
        }.runTaskLater(app, 6000)
    }

    override fun onSabotageEnd(isRepaired: Boolean) {
        sabotageRunnable?.takeIf { !it.isCancelled } ?: return
        sabotageRunnable = null
        brokenBlock = 0

        val title = if (isRepaired) {
            Title.title(
                "Поломка устанена".asText(NamedTextColor.GREEN),
                Component.empty(),
                Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
            )
        } else {
            players.forEach {
                it.playSound(it.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1, true, false))
            }
            end(true)
            return
        }

        players.forEach {
            it.showTitle(title)
            it.sendMessage(Component.text("Поломка устранёна", NamedTextColor.GREEN))
        }

        sabotageRunnable?.cancel()
        sabotageRunnable = null
    }

    override fun startVoting(whoStarted: Player) {
        if (sabotageRunnable != null) {
            whoStarted.sendMessage(Component.text("Нельзя начать собрание, так-как что-то сломано...", NamedTextColor.RED))
            return
        }
        if (isNight) {
            whoStarted.sendMessage(Component.text("Нельзя начать голосование ночью, весь экипаж устал...", NamedTextColor.RED))
            return
        }

        val message = "Выберите кого вы хотите изгнать".asText(NamedTextColor.RED)
        val title = Title.title(
            "ГОЛОСОВАНИЕ".asText(NamedTextColor.RED),
            message,
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

        val result = when {
            maxVotes == null || totalVotes == 0 || skipVotes >= maxVotes ->
                "Голосование пропущено" to NamedTextColor.GREEN
            votingMap.values.count { it == maxVotes } > 1 ->
                "Голосование не дало результата" to NamedTextColor.RED
            else -> {
                val name = votingMap.entries.first { it.value == maxVotes }.key
                val player = Bukkit.getPlayerExact(name)

                if (player == null || !player.isOnline) {
                    "Игрок $name покинул игру, решил выйти самостоятельно" to NamedTextColor.YELLOW
                } else {
                    onMafiaKill(player)
                    "Был изгнан игрок с именем: $name" to NamedTextColor.RED
                }
            }
        }

        val (message, color) = result
        val title = if (message.contains("изгнан")) "Прощай $message" else message

        val finalMessage = Component.text(message, color)
        val finalTitle = Title.title(
            "ГОЛОСОВАНИЕ".asText(NamedTextColor.GREEN),
            Component.text(title, color),
            Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1400), Duration.ofMillis(500))
        )

        players.forEach {
            it.showTitle(finalTitle)
            it.sendMessage(finalMessage)
            it.inventory.remove(Material.ENCHANTED_BOOK)
        }

        votingMap.clear()
        skipVoters.clear()
        kickVoters.clear()

        isVoting = false
    }

    override fun openVotingMenu(player: Player) {
        val alivePlayers = players.filter { !killedPlayers.contains(it) }

        val inventorySize = (alivePlayers.size + 1).toInventorySize()

        val inventory = Bukkit.createInventory(null, inventorySize, "Голосование")

        alivePlayers.forEach {
            val item = NamedItemStack(Material.PLAYER_HEAD, it.name)
            inventory.addItem(item)
        }

        val barrier = NamedItemStack(Material.BARRIER, "Пропустить")
        inventory.addItem(barrier)

        player.openInventory(inventory)
    }

    override fun startDayNightCycle() {
        timeRunnable = Bukkit.getScheduler().runTaskLater(app, Runnable {
            startNight()
        }, 12000)
    }

    override fun startNight() {
        isNight = true
        val title = Title.title(
            "Ночь наступила!".asText(NamedTextColor.BLUE),
            "Иконка ночи".asText()
        )
        bossBar.name(Component.text("Ночь | $dayCount "))
        bossBar.color(BossBar.Color.RED)

        players.forEach { player ->
            player.showTitle(title)
            player.sendMessage("Ночь наступила!".asText(NamedTextColor.BLUE))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 4800, 4, true, false))
        }

        timeRunnable = Bukkit.getScheduler().runTaskLater(app, Runnable {
             endNight()
        }, 4800)
    }

    override fun endNight() {
        sleepingPlayers.clear()
        isNight = false
        // fillChestInventories(chestInventories.keys.toList())
        val title = Title.title(
            "День наступил!".asText(NamedTextColor.YELLOW),
            "Иконка солнца".asText(NamedTextColor.GREEN)
        )

        bossBar.name("День | $dayCount ".asText())
        bossBar.color(BossBar.Color.GREEN)

        players.forEach { player ->
            player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5 * 60, 1, true, false))
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            player.removePotionEffect(PotionEffectType.SLOW)
            player.showTitle(title)
            player.sendMessage("День наступил!".asText(NamedTextColor.YELLOW))
            player.wakeup(false)
        }

        dayCount += 1

        if (dayCount == 6) {
            end(false)
            return
        }

        timeRunnable = Bukkit.getScheduler().runTaskLater(app, Runnable {
            startNight()
        }, 12000)
    }

    override fun onPlayerClickBed(player: Player, location: Location) {
        if (!isNight) return
        val alive = players.size - killedPlayers.size
        println(alive)
        sleepingPlayers.add(player.uniqueId)
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 4800, 3, true, false))
        player.sleep(location, true)
        println(ceil(alive / 2.0))
        if (sleepingPlayers.size >= alive / 2) {
            endNight()
        }
    }


    override fun checkGameEnd() {
        val alivePlayers = players.filter { !killedPlayers.contains(it) }
        val mafiaPlayers = alivePlayers.filter { it.currentRole?.canKill == true }
        val nonMafiaPlayers = alivePlayers.filter { it.currentRole?.canKill != true }

        if (mafiaPlayers.isEmpty()) {
            end(false)
        } else if (nonMafiaPlayers.isEmpty() || nonMafiaPlayers.size <= mafiaPlayers.size) {
            end(true)
        }
    }

    fun fillChestInventories(locations: List<Location>) {
        val inventories = LinkedList<Inventory>()
        locations.forEach { location ->
            val inventory = generateRandomInventory()
            chestInventories[location] = inventory
            inventories.add(inventory)
        }

        inventories.shuffle()

        inventories.take(2).forEach { inventory ->
            val pickaxe = ItemStack(Material.IRON_PICKAXE)
            inventory.addItem(pickaxe)
        }
    }
}