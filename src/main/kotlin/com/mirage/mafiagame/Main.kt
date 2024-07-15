package com.mirage.mafiagame

import com.mirage.mafiagame.command.TestCMD
import com.mirage.mafiagame.network.listeners.MessageHandler
import com.mirage.mafiagame.service.QueueService
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    lateinit var queueService: QueueService

    override fun onEnable() {
        // Initialize services manually
        queueService = QueueService(this)

        // Register commands and listeners
        getCommand("testcmd")?.setExecutor(TestCMD(this))
        server.messenger.registerIncomingPluginChannel(this, "queues:addplayer", MessageHandler(queueService))
    }
}
