package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.game.impl.MafiaGame
import com.mirage.mafiagame.game.listener.BlockBreakListener
import com.mirage.mafiagame.network.listener.QueueJoinMessage
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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
        PacketEvents.getAPI().eventManager.registerListener(BlockUpdateListener, PacketListenerPriority.HIGH)
        server.messenger.registerIncomingPluginChannel(this, "mafia:queue", QueueJoinMessage(queueService))
        server.messenger.registerOutgoingPluginChannel(this, "mafia:queue")

        getCommand("testcmd")?.setExecutor(this)
        PacketEvents.getAPI().init()
    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val game = MafiaGame(this, listOf(sender))
            sender.currentGame = game
            game.onMafiaKill(sender)
        }
        return true
    }
}
