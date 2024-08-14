package com.mirage.mafiagame.network.listener

import com.mirage.mafiagame.network.NetUtil
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.koin.core.component.inject
import java.nio.ByteBuffer

class QueueJoinListener(override val app: ServerPlugin) : PluginMessageListener, PluginService {
    val queueService by inject<QueueService>()

    fun registerChannels() {
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mafia:queue", this)
    }

    fun unregisterChannels() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(app, "mafia:queue", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "mafia:queue") return

        val queueTypeCode = ByteBuffer.wrap(message)
        val code = NetUtil.readInt(queueTypeCode)
        val queueType = QueueType.entries.find { it.ordinal == code }

        queueType?.let {
            queueService.joinQueue(player, it)
        }
    }
}
