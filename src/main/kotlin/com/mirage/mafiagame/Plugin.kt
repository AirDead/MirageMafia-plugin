package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.mirage.mafiagame.game.listener.BlockBreakListener
import com.mirage.mafiagame.game.listener.BlockInteractionListener
import com.mirage.mafiagame.game.listener.DropItemListener
import com.mirage.mafiagame.network.listener.QueueJoinMessage
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().settings.reEncodeByDefault(false).checkForUpdates(true)
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        val queueService = QueueService(this)

        server.pluginManager.registerEvents(BlockBreakListener(this), this)
        server.pluginManager.registerEvents(BlockInteractionListener, this)
        server.pluginManager.registerEvents(DropItemListener, this)
        server.messenger.registerIncomingPluginChannel(this, "mafia:queue", QueueJoinMessage(queueService))
        server.messenger.registerOutgoingPluginChannel(this, "mafia:queue")

        PacketEvents.getAPI().eventManager.registerListener(BlockUpdateListener, PacketListenerPriority.HIGH)
        PacketEvents.getAPI().init()
    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

}
