package com.mirage.mafiagame.network.listeners

import com.mirage.mafiagame.network.NetUtil
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.nio.ByteBuffer

class TestListener : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel == "miragemafia:removeplayer") {
            if (message != null) {
                val buffer = ByteBuffer.wrap(message)

                val intValue = NetUtil.readInt(buffer)
                val stringValue = NetUtil.readString(buffer)

                player.sendMessage("Received Int: $intValue")
                player.sendMessage("Received String: $stringValue")
            } else {
                player.sendMessage("Message is null")
            }
        }
    }
}
