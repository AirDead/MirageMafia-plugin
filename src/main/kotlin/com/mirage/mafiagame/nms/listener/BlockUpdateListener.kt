package com.mirage.mafiagame.nms.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import com.mirage.mafiagame.nms.block.updatedLocations

object BlockUpdateListener : PacketListener {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            val packet = WrapperPlayServerBlockChange(event)
            if (updatedLocations.contains(packet.blockPosition)) {
                event.isCancelled = true
            }
        }
    }
}

