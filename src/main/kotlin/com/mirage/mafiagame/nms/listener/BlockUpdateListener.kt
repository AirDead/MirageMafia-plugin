package com.mirage.mafiagame.nms.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import com.mirage.mafiagame.game.currentGame
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object BlockUpdateListener : PacketListener {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            val packet = WrapperPlayServerBlockChange(event)
            val player = event.user.getBukkitPlayer()

            player?.currentGame?.let { game ->
                if (game.updatedLocations.contains(packet.blockPosition)) {
                    event.isCancelled = true
                }
            }
        }
    }
}


fun User.getBukkitPlayer(): Player? {
    return Bukkit.getPlayer(this.profile.uuid)
}