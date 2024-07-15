package com.mirage.mafiagame.command

import com.mirage.mafiagame.network.ModTransfer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TestCMD(val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val arg = args?.getOrNull(0) ?: "test"
            ModTransfer(arg).send(plugin, "queues:addplayer", sender)
        }
        return true
    }
}
