@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame

import com.github.retrooper.packetevents.PacketEvents
import com.mirage.mafiagame.command.MafiaCommand
import com.mirage.mafiagame.command.SudoCommand
import com.mirage.mafiagame.config.ConfigService
import com.mirage.mafiagame.config.ConfigServiceImpl
import com.mirage.mafiagame.game.listener.*
import com.mirage.mafiagame.network.listener.QueueJoinService
import com.mirage.mafiagame.nms.listener.BlockUpdateListener
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueServiceImpl
import com.mirage.mafiagame.role.RoleService
import com.mirage.mafiagame.role.RoleServiceImpl
import com.mirage.mafiagame.task.TaskService
import dev.nikdekur.minelib.koin.loadModule
import dev.nikdekur.minelib.plugin.ServerPlugin
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.koin.dsl.bind

class Plugin : ServerPlugin() {

    override val components = listOf(
        // Services
        QueueServiceImpl(this),
        TaskService(this),
        QueueJoinService(this),
        RoleServiceImpl(this),
        ConfigServiceImpl(this),

        // Commands
        MafiaCommand(),
        SudoCommand(),

        // Listeners
        BlockListener(this),
        BlockUpdateListener(this),
        ChatListener(this),
        InteractionListener(this),
        ItemListener(this),
        PlayerListener(this)
    )

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

        loadModule {
            single { modulesManager.getModule<QueueServiceImpl>() } bind QueueService::class
            single { modulesManager.getModule<RoleServiceImpl>() } bind RoleService::class
            single { modulesManager.getModule<ConfigServiceImpl>() } bind ConfigService::class
        }


    }


    override fun whenDisable() {
        PacketEvents.getAPI().terminate()
    }
}