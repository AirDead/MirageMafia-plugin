@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.mirage.mafiagame.command.MafiaCommand
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.game.listener.*
import com.mirage.mafiagame.network.listener.QueueJoinService
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.role.RoleAssignService
import com.mirage.mafiagame.task.TaskService
import com.mirage.mafiagame.tooling.LocationService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    val modules = listOf(
        // Services
        QueueService(this),
        TaskService(this),
        QueueJoinService(this),
        RoleAssignService(this),
        TaskService(this),
        LocationService(this),

        // Commands
        MafiaCommand(this),

        // Listeners
        BlockListener(this),
        ChatListener(this),
        InteractionListener(this),
        ItemListener(this),
        PlayerListener(this),
        BlockUpdateListener(this)
    )

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().settings.reEncodeByDefault(false).checkForUpdates(true)
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        saveDefaultConfig()
        loadModules()
        PacketEvents.getAPI().init()

        getCommand("sudo")?.setExecutor(SudoCommand)
    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

    inline fun loadModules() {
        modules.forEach { it.onLoad() }
    }
}




