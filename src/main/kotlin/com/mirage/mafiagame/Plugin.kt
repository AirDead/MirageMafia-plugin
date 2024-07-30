package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.mirage.mafiagame.command.MafiaCommand
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.game.listener.*
import com.mirage.mafiagame.network.listener.QueueJoinMessage
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    private val locations = mutableListOf<Location>()

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().settings.reEncodeByDefault(false).checkForUpdates(true)
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        val queueService = QueueService(this)

        saveDefaultConfig()

        server.pluginManager.registerEvents(BlockListener(this), this)
        server.pluginManager.registerEvents(PlayerListener(this), this)
        server.pluginManager.registerEvents(InteractionListener, this)
        server.pluginManager.registerEvents(ItemListener, this)
        server.pluginManager.registerEvents(ChatListener, this)

        server.messenger.registerIncomingPluginChannel(this, "mafia:queue", QueueJoinMessage(queueService))
        server.messenger.registerOutgoingPluginChannel(this, "mafia:queue")

        PacketEvents.getAPI().eventManager.registerListener(BlockUpdateListener, PacketListenerPriority.NORMAL)
        PacketEvents.getAPI().init()

        getCommand("mafia")?.setExecutor(MafiaCommand(queueService))
        getCommand("sudo")?.setExecutor(SudoCommand)
    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }
}




