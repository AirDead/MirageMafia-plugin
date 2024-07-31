package com.mirage.mafiagame.nms.listener

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.packetapi.extensions.sendPackets
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class BlockUpdateListener(app: JavaPlugin) : BaseModule<BlockUpdateListener>(app) {

    override fun onLoad() {
        PacketEvents.getAPI().eventManager.registerListener(BlockUpdatePacketListener)
    }

    override fun onUnload() {
        PacketEvents.getAPI().eventManager.unregisterListener(BlockUpdatePacketListener)
    }
}

object BlockUpdatePacketListener : PacketListenerAbstract(PacketListenerPriority.NORMAL) {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            val player = event.user.getBukkitPlayer()
            val packet = WrapperPlayServerBlockChange(event)
            player?.currentGame?.let { game ->
                if (game.updatedLocations.contains(packet.blockPosition)) {
                    val newPacket = ClientboundBlockUpdatePacket(packet.blockPosition.toBlockPos(), Blocks.RED_CONCRETE.defaultBlockState())
                    player.sendPackets(newPacket)
                }
            }
        }
    }
}

fun User.getBukkitPlayer(): Player? {
    return Bukkit.getPlayer(this.profile.uuid)
}