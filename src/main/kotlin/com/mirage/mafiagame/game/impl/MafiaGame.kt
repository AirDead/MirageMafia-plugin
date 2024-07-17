package com.mirage.mafiagame.game.impl

import com.mirage.mafiagame.game.Game
import com.mirage.mafiagame.nms.player.privateWorld
import com.mirage.mafiagame.role.currentRole
import net.minecraft.world.level.block.Blocks
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class MafiaGame(
    override val plugin: JavaPlugin,
    override val players: List<Player>
) : Game {
    override var brokenBlock: Int = 0
    override val isSabotageStart: Boolean = false
    var sabotageRunnable : BukkitTask? = null

    override fun start() {
        println("Game started")
    }

    override fun onMafiaKill(player: Player) {


    }

    override fun onBlockBreak(player: Player, block: Block) {
        when (block.type) {
            Material.YELLOW_CONCRETE_POWDER -> {
                if (player.currentRole?.canBreak == true) {
                    brokenBlock += 1
                    if (brokenBlock >= 5) onSabotageStart()
                    player.privateWorld?.changeBlockState(block, Blocks.RED_CONCRETE_POWDER.defaultBlockState())
                }
            }
            Material.RED_TERRACOTTA -> {
                if (player.currentRole?.canRepair == true) {
                    if (!isSabotageStart) return
                    brokenBlock -= 1
                    if (brokenBlock <= 0) onSabotageEnd(true)
                    player.privateWorld?.changeBlockState(block, Blocks.YELLOW_CONCRETE_POWDER.defaultBlockState())
                }
            }
            else -> {}
        }
    }

    override fun onSabotageStart() {
        players.forEach { player ->
            player.sendTitle("САБОТАЖ.", "У ВАС ЕСТЬ 5 МИНУТ НА УСТРАНЕНИЕ", 10, 70, 20)
        }

        sabotageRunnable = object : BukkitRunnable() {
            override fun run() {
                onSabotageEnd(false)
            }

        }.runTaskLater(plugin, 20 * 300)
    }


    override fun onSabotageEnd(isRepaired: Boolean) {
        if (sabotageRunnable?.isCancelled == true) return

        if (!isRepaired) {
            players.forEach { player ->
                player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1))
                player.teleport(player.world.spawnLocation) // TODO: Или из кфг, или локация спавна мира как лобби
            }
        } else {
            players.forEach { player ->
                player.sendTitle("САБОТАЖ УСПЕШНО УСТРАНЁН", "", 10, 70, 20)
            }
        }
    }
}