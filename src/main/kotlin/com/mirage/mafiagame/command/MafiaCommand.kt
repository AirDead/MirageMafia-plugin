package com.mirage.mafiagame.command

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.module.module
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MafiaCommand(app: JavaPlugin) : BaseModule(app), CommandExecutor {
    val queueService: QueueService by module()

    override fun onLoad() {
        app.getCommand("mafia")?.setExecutor(this)
    }

    override fun onUnload() {
        app.getCommand("mafia")?.setExecutor(null)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return true
        }

        if (sender.currentGame == null) {
            queueService.joinQueue(sender, QueueType.FIRST)
            sender.sendMessage(
                Component.text("Вы зашли в очередь").color(NamedTextColor.GREEN)
            )
        } else {
            sender.sendMessage(
                Component.text("Вы уже находитесь в игре").color(NamedTextColor.RED)
            )
        }

        return true
    }
}