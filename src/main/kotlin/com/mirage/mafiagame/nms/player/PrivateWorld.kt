package com.mirage.mafiagame.nms.player

import com.mirage.packetapi.extensions.sendPackets
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PrivateWorld(
    val plugin: JavaPlugin,
    val players: List<Player>
) {
    fun changeBlockState(block: Block, newBlockState: BlockState) {
        val packet = ClientboundBlockUpdatePacket(BlockPos(block.x, block.y, block.z), newBlockState)
        players.forEach { it.sendPackets(packet) }
    }
}

private val playerPrivateWorldMap = mutableMapOf<Player, PrivateWorld?>()

var Player.privateWorld: PrivateWorld?
    get() = playerPrivateWorldMap[this]
    set(value) {
        playerPrivateWorldMap[this]?.let { previousWorld ->
            Bukkit.getOnlinePlayers().forEach { outsidePlayer ->
                if (!previousWorld.players.contains(outsidePlayer)) {
                    previousWorld.players.forEach {
                        it.showPlayer(previousWorld.plugin, outsidePlayer)
                        outsidePlayer.showPlayer(previousWorld.plugin, it)
                    }
                }
            }
        }
        playerPrivateWorldMap[this] = value
        value?.let { newWorld ->
            Bukkit.getOnlinePlayers().forEach { outsidePlayer ->
                if (!newWorld.players.contains(outsidePlayer)) {
                    newWorld.players.forEach {
                        it.hidePlayer(newWorld.plugin, outsidePlayer)
                        outsidePlayer.hidePlayer(newWorld.plugin, it)
                    }
                }
            }
        }
    }