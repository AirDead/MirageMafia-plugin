package com.mirage.mafiagame.network.listeners

import com.mirage.mafiagame.network.NetUtil
import com.mirage.mafiagame.service.QueueService
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.nio.ByteBuffer

class MessageHandler(private val queueService: QueueService) : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "queues:addplayer") {
            return
        }

        val buffer = ByteBuffer.wrap(message)
        try {
            val receivedData = NetUtil.readString(buffer)
            println(receivedData)
            queueService.queue1.addPlayerToQueue(com.mirage.utils.models.Player(receivedData)) // Adding player to queue
            println(queueService.queue1.getCurrentPeopleCount())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
