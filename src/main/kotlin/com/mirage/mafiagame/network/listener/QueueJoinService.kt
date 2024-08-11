package com.mirage.mafiagame.network.listener

import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.network.NetUtil
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.koin.core.component.inject
import java.nio.ByteBuffer

class QueueJoinService(app: ServerPlugin) : BaseModule(app), PluginMessageListener{
    val queueService by inject<QueueService>()

    override fun onLoad() {
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mafia:queue", this)
    }

    override fun onUnload() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(app, "mafia:queue", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val queueTypeCode = ByteBuffer.wrap(message)
        val code = NetUtil.readInt(queueTypeCode)
        val queueType = QueueType.entries.find { it.ordinal == code }

        if (queueType != null) {
            queueService.joinQueue(player, queueType)
        }
    }
}