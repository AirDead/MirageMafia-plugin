package com.mirage.mafiagame.nms.listener

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.toBlockPos
import com.mirage.packetapi.extensions.sendPackets
import dev.nikdekur.ndkore.module.Module
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BlockUpdateListener(override val app: JavaPlugin) :
    PacketListenerAbstract(PacketListenerPriority.NORMAL), Module<JavaPlugin> {

    override fun onLoad() {
        PacketEvents.getAPI().eventManager.registerListener(this)
    }

    override fun onUnload() {
        PacketEvents.getAPI().eventManager.unregisterListener(this)
    }

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            val player = Bukkit.getPlayer(event.user.uuid)
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