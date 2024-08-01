@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.mirage.mafiagame.command.MafiaCommand
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.config.ConfigService
import com.mirage.mafiagame.game.listener.*
import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.module.DIContainer
import com.mirage.mafiagame.network.listener.QueueJoinService
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.role.RoleAssignService
import com.mirage.mafiagame.task.TaskService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    val modules = listOf<BaseModule>(
        // Services
        QueueService(this),
        TaskService(this),
        QueueJoinService(this),
        RoleAssignService(this),
        ConfigService(this),

        // Commands
        MafiaCommand(this),
        SudoCommand(this),

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
    }

    override fun onDisable() {
        unloadModules()
        PacketEvents.getAPI().terminate()
    }

    inline fun loadModules() {
        modules.forEach {
            logger.info("Loading module: ${it::class.simpleName}")
            it.onLoad()
            DIContainer.register(it::class.java, it)
            logger.info("Loaded module: ${it::class.simpleName}")
        }
    }

    inline fun unloadModules() {
        modules.forEach {
            logger.info("Unloading module: ${it::class.simpleName}")
            it.onUnload()
            DIContainer.unregister(it::class.java)
            logger.info("Unloaded module: ${it::class.simpleName}")
        }
    }
}