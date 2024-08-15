package com.mirage.mafiagame.network.listener

import dev.nikdekur.minelib.koin.MineLibKoinComponent
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class QueueJoinListener : PluginMessageListener, MineLibKoinComponent {

//    fun registerChannels() {
//        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mafia:queue", this)
//    }
//
//    fun unregisterChannels() {
//        Bukkit.getMessenger().unregisterIncomingPluginChannel(app, "mafia:queue", this)
//    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "mafia:queue") return

//        val queueTypeCode = ByteBuffer.wrap(message)
//        val code = NetUtil.readInt(queueTypeCode)
//        val queueType = QueueType.entries.find { it.ordinal == code }
//
//        queueType?.let {
//            queueService.joinQueue(player, it)
//        }
    }
}
