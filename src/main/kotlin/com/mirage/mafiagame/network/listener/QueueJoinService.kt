package com.mirage.mafiagame.network.listener

import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.module.module
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.nio.ByteBuffer

class QueueJoinService(app: JavaPlugin) : BaseModule(app), PluginMessageListener {
    private val queueService: QueueService by module()

    override fun onLoad() {
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mafia:queue", this)
    }

    override fun onUnload() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(app, "mafia:queue", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel == "mafia:queue" && message != null) {
            val queueTypeCode = ByteBuffer.wrap(message).int
            val queueType = QueueType.entries.find { it.code == queueTypeCode }

            if (queueType != null) {
                queueService.joinQueue(player, queueType)
            }
        }
    }
}