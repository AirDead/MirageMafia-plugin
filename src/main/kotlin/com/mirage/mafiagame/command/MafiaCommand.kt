package com.mirage.mafiagame.command

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.module.module
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MafiaCommand(app: JavaPlugin) : BaseModule(app), CommandExecutor {
    private val queueService: QueueService by module()

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

        val onlinePlayers = Bukkit.getOnlinePlayers()

        if (sender.currentGame == null) {
            queueService.joinQueue(sender, QueueType.FIRST)
            sender.sendMessage(
                Component.text("Вы зашли в очередь").color(NamedTextColor.GREEN)
            )
        } else if (args.isNotEmpty()) {
            when (args[0]) {
                "start" -> {
                    sender.currentGame?.startVoting(sender)
                }
                "stop" -> {
                    sender.currentGame?.endVoting()
                }
                "location" -> {
                    val targetBlock = sender.getTargetBlock(null, 5) // 5 - это максимальная дистанция, можно изменить при необходимости
                    val location = targetBlock.location
                    sender.sendMessage(
                        Component.text("Вы смотрите на блок с локацией: x=${location.x}, y=${location.y}, z=${location.z}").color(NamedTextColor.YELLOW)
                    )
                }
                else -> {
                    sender.sendMessage(Component.text("Unknown command").color(NamedTextColor.RED))
                }
            }
        } else {
            sender.sendMessage(Component.text("Usage: /mafia <command>").color(NamedTextColor.RED))
        }

        return true
    }
}