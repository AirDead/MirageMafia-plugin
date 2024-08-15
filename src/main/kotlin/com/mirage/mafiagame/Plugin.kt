package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.game.listener.*
import com.mirage.mafiagame.network.listener.QueueJoinListener
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueManagerService
import dev.nikdekur.minelib.plugin.ServerPlugin
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder

class Plugin : ServerPlugin() {
    override val components by lazy {
        listOf(
//            // Services
            QueueManagerService(this),
//            PlayerTaskManager(this),
//            RoleAssignmentService(this),
//            LocationManagerService(this),
            BlockUpdateListener(this),
////
//            // Commands
//            MafiaCommand(this),
            SudoCommand(this),

            // Listeners
            BlockListener(this),
            ChatListener(),
            InteractionListener(),
            ItemListener(),
            QueueJoinListener(),
            PlayerListener(this)
        )
    }

    override fun whenLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().apply {
            settings.reEncodeByDefault(false).checkForUpdates(true)
            load()
        }
    }

    override fun whenEnabled() {
        PacketEvents.getAPI().init()
    }

    override fun afterReload() {
        saveDefaultConfig()
    }

    override fun whenDisable() {
        PacketEvents.getAPI().terminate()
    }
}