package com.mirage.mafiagame.nms.listener

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import com.mirage.mafiagame.ext.sendPackets
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.block.toBlockPos
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.service.PluginService
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit

class BlockUpdateListener(override val app: ServerPlugin) :
    PacketListenerAbstract(PacketListenerPriority.NORMAL), PluginService {

    override fun onLoad() {
        PacketEvents.getAPI().eventManager.registerListener(this)
    }

    override fun onUnload() {
        PacketEvents.getAPI().eventManager.unregisterListener(this)
    }

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType != PacketType.Play.Server.BLOCK_CHANGE) return

        val player = Bukkit.getPlayer(event.user.uuid) ?: return
        val game = player.currentGame ?: return
        val packet = WrapperPlayServerBlockChange(event)

        if (packet.blockPosition in game.updatedLocations) {
            val newBlockState = Blocks.RED_CONCRETE.defaultBlockState()
            val newPacket = ClientboundBlockUpdatePacket(packet.blockPosition.toBlockPos(), newBlockState)
            player.sendPackets(newPacket)
        }
    }
}