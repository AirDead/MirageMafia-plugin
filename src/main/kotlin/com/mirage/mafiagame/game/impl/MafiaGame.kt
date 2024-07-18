package com.mirage.mafiagame.game.impl

import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.BlockTypeStorage
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.mafiagame.nms.block.toVector3i
import com.mirage.mafiagame.nms.block.updatedLocations
import com.mirage.mafiagame.nms.npc.Corpse
import com.mirage.mafiagame.role.currentRole
import com.mirage.packetapi.extensions.craftPlayer
import com.mirage.packetapi.extensions.sendPackets
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import net.minecraft.world.level.block.Blocks
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

class MafiaGame(
    override val plugin: JavaPlugin,
    override val players: List<Player>
) : Game {
    override var brokenBlock: Int = 0
    override val completedTasks = mutableListOf<Int>()
    override var sabotageRunnable: BukkitTask? = null
    override var lastSabotageEndTime: Long = 0

    override fun start() {
        println("Game started")
    }

    override fun end() {
        println("Game ended")
    }

    override fun onMafiaKill(player: Player) {
        val location = player.location
        
        val tabPacketInfo = ClientboundPlayerInfoUpdatePacket.Entry(
            player.uniqueId,
            player.craftPlayer.profile,
            true,
            0,
            GameType.SURVIVAL,
            Component.literal(player.name),
            null
        )
        val tabPacket = ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME), tabPacketInfo)

        player.gameMode = GameMode.SURVIVAL

        players.forEach {
            Corpse.spawnCorpse(it, player.name, player.uniqueId, location.x, location.y, location.z)
            it.sendPackets(tabPacket)
        }
    }

    override fun onBlockBreak(player: Player, block: Block) {
        val location = block.location
        val currentRole = player.currentRole
        val currentTime = System.currentTimeMillis()

        val (newType, newState) = when {
            currentRole?.canRepair == true && block.type == Material.RED_CONCRETE && sabotageRunnable != null -> Material.YELLOW_CONCRETE to Blocks.YELLOW_CONCRETE.defaultBlockState()
            currentRole?.canBreak == true && block.type == Material.YELLOW_CONCRETE && sabotageRunnable == null && currentTime - lastSabotageEndTime > 180_000 -> Material.RED_CONCRETE to Blocks.RED_CONCRETE.defaultBlockState()
            currentRole?.canBreak == true && block.type == Material.YELLOW_CONCRETE && sabotageRunnable == null && currentTime - lastSabotageEndTime <= 180_000 -> {
                player.sendMessage("Саботаж на кулдауне. Подождите ${(180_000 - (currentTime - lastSabotageEndTime)) / 1000} секунд.")
                return
            }
            else -> return
        }

        players.forEach { it.sendPackets(ClientboundBlockUpdatePacket(location.toBlockPos(), newState)) }
        BlockTypeStorage.updateBlockType(location, newType)
        val vector = location.toVector3i()

        when (newType) {
            Material.RED_CONCRETE -> {
                updatedLocations.add(vector)
                if (++brokenBlock >= 5 && sabotageRunnable == null) {
                    onSabotageStart()
                }
            }
            Material.YELLOW_CONCRETE -> {
                updatedLocations.remove(vector)
                if (--brokenBlock == 0 && sabotageRunnable != null) {
                    onSabotageEnd(true)
                }
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
                player.currentRole = null
                player.currentGame = null
                player.teleport(player.world.spawnLocation)
            }
        }
        lastSabotageEndTime = System.currentTimeMillis()
    }
}
