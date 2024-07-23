package com.mirage.mafiagame.network.listener

import com.mirage.mafiagame.network.NetUtil
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.nio.ByteBuffer

class QueueJoinMessage(
    val queueService: QueueService
) : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel == "mafia:queue" && message != null) {
            val queueTypeCode = NetUtil.readInt(ByteBuffer.wrap(message))
            val queueType = QueueType.entries.find { it.code == queueTypeCode }

            if (queueType != null) {
                queueService.joinQueue(player, queueType)
            }
        }
    }
}