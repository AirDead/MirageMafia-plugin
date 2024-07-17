package com.mirage.mafiagame

import com.mirage.mafiagame.command.TestCmd
import com.mirage.mafiagame.network.listener.QueueJoinMessage
import com.mirage.mafiagame.queue.QueueService
import org.bukkit.plugin.java.JavaPlugin


class Plugin : JavaPlugin() {
    override fun onEnable() {
        val queueService = QueueService(this)

        server.messenger.registerIncomingPluginChannel(this, "mafia:queue", QueueJoinMessage(queueService))
        server.messenger.registerOutgoingPluginChannel(this, "mafia:queue")

        getCommand("testcmd")?.setExecutor(TestCmd())
    }
}
