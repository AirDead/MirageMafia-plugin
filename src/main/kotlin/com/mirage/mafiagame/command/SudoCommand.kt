package com.mirage.mafiagame.command

import com.mirage.mafiagame.module.BaseModule
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class SudoCommand(app: JavaPlugin) : BaseModule(app), CommandExecutor {

    override fun onLoad() {
        app.getCommand("sudo")?.setExecutor(this)
    }

    override fun onUnload() {
        app.getCommand("sudo")?.setExecutor(null)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.isOp) return false
        if (args == null || args.size < 2) return false

        val name = args[0]
        val cmd = args.drop(1).joinToString(" ")

        val player = Bukkit.getPlayer(name) ?: return false

        player.performCommand(cmd)

        return true
    }
}
