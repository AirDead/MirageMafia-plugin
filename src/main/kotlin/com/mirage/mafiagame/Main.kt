package com.mirage.mafiagame

import com.mirage.mafiagame.network.ModTransfer
import com.mirage.mafiagame.network.listeners.TestListener
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
//    private val moduleManager = ModulesManager<Main>().apply {
//        addModule(AttackListener(this@Main))
//        addModule(ConfigManager(this@Main))
//    }

//    lateinit var configManager: ConfigManager
    override fun onEnable() {
        // Инициализация и регистрация слушателя для канала
        saveDefaultConfig()
//        moduleManager.loadAll()
        getCommand("test")?.setExecutor(this)
        server.messenger.registerIncomingPluginChannel(this, "miragemafia:removeplayer", TestListener())
        server.messenger.registerOutgoingPluginChannel(this, "miragemafia:removeplayer")


    }

    override fun onDisable() {
        server.messenger.unregisterIncomingPluginChannel(this)
        server.messenger.unregisterOutgoingPluginChannel(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("test", ignoreCase = true)) {
            if (sender is Player) {
                sendTestMessage(sender)
                sender.sendMessage("Test message sent!")
                return true
            } else {
                sender.sendMessage("This command can only be run by a player.")
                return true
            }
        }
        return false
    }

    private fun sendTestMessage(player: Player) {
        ModTransfer(1, "Test").send(this, "miragemafia:createmenu", player)
    }
}
