package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.mirage.mafiagame.command.MafiaCommand
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.game.listener.BlockListener
import com.mirage.mafiagame.game.listener.ChatListener
import com.mirage.mafiagame.game.listener.InteractionListener
import com.mirage.mafiagame.game.listener.ItemListener
import com.mirage.mafiagame.game.listener.PlayerListener
import com.mirage.mafiagame.location.ConfigLocationService
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.ConfigQueueService
import com.mirage.mafiagame.role.RoleAssignmentService
import com.mirage.mafiagame.task.PlayerTaskManager
import dev.nikdekur.minelib.plugin.ServerPlugin
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder

class Plugin : ServerPlugin() {
    override val components by lazy {
        listOf(
            // Service
            ConfigLocationService(this),
            ConfigQueueService(this),
            RoleAssignmentService(this),
            PlayerTaskManager(this),
            BlockUpdateListener(this),

            // Commands
            SudoCommand(this),
            MafiaCommand(this),

            // Listener
            BlockListener(this),
            ChatListener(this),
            InteractionListener(this),
            ItemListener(),
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